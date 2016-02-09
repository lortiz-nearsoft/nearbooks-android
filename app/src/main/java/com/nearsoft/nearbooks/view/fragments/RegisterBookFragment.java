package com.nearsoft.nearbooks.view.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentRegisterBookBinding;
import com.nearsoft.nearbooks.databinding.ItemGoogleBooksVolumeBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.GoogleBooksModel;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.activities.zxing.CaptureActivityAnyOrientation;
import com.nearsoft.nearbooks.view.adapters.GoogleBooksVolumeAdapter;
import com.nearsoft.nearbooks.ws.bodies.GoogleBookBody;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;

import java.util.List;

import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterBookFragment extends BaseFragment
        implements GoogleBooksVolumeAdapter.OnGoogleBookItemClickListener {

    private final static String KEY_CODE = "KEY_CODE";

    private FragmentRegisterBookBinding mBinding;
    private GoogleBooksVolumeAdapter mGoogleBooksVolumeAdapter;
    private String mCode;

    public RegisterBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegisterBookFragment.
     */
    public static RegisterBookFragment newInstance() {
        RegisterBookFragment fragment = new RegisterBookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleBooksVolumeAdapter = new GoogleBooksVolumeAdapter(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CODE)) {
            mCode = savedInstanceState.getString(KEY_CODE);
            findBooksByIsbn();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentRegisterBookBinding.inflate(inflater, container, false);
        mBinding.fabScanQrCode.setOnClickListener(v -> startQRScanner());

        mBinding.recyclerViewBooks.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerViewBooks.setLayoutManager(layoutManager);
        mBinding.recyclerViewBooks.setAdapter(mGoogleBooksVolumeAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CODE, mCode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator
                .parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && scanResult.getContents() != null) {
            mCode = scanResult.getContents();
            findBooksByIsbn();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void findBooksByIsbn() {
        ProgressDialog progressDialog = ProgressDialog
                .show(getContext(), getString(R.string.message_getting_book_info), null, true);
        subscribeToFragment(GoogleBooksModel.findGoogleBooksByIsbn(mCode)
                .subscribe(new Subscriber<List<GoogleBookBody>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable t) {
                        progressDialog.dismiss();
                        ViewUtil.showSnackbarMessage(mBinding, ErrorUtil
                                .getMessageFromThrowable(t, getContext()));
                    }

                    @Override
                    public void onNext(List<GoogleBookBody> googleBookBodies) {
                        progressDialog.dismiss();
                        mGoogleBooksVolumeAdapter.setGoogleBookBodies(googleBookBodies);
                        showResults(!googleBookBodies.isEmpty());
                    }
                }));
    }

    private void showResults(boolean showResults) {
        if (showResults) {
            mBinding.recyclerViewBooks.setVisibility(View.VISIBLE);
            mBinding.tvEmpty.setVisibility(View.GONE);
        } else {
            mBinding.recyclerViewBooks.setVisibility(View.GONE);
            mBinding.tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(getString(R.string.message_scan_book_isbn_bar_code));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onGoogleBookItemClicked(ItemGoogleBooksVolumeBinding binding) {
        final GoogleBookBody googleBookBody = binding.getBook();
        new AlertDialog
                .Builder(getContext())
                .setTitle(R.string.question_register_new_book)
                .setMessage(
                        getString(R.string.message_book_resume, googleBookBody.getTitle(),
                                googleBookBody.getAuthors(), googleBookBody.getPublishedDate())
                )
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    subscribeToFragment(BookModel.registerNewBook(googleBookBody)
                            .doOnError(t -> ViewUtil.showSnackbarMessage(mBinding,
                                    t.getLocalizedMessage()))
                            .subscribe(response -> {
                                if (response.isSuccess()) {
                                    ViewUtil.showToastMessage(getContext(), R.string.message_done);
                                } else {
                                    MessageResponse messageResponse = ErrorUtil
                                            .parseError(MessageResponse.class, response);
                                    if (messageResponse != null) {
                                        ViewUtil.showSnackbarMessage(mBinding,
                                                messageResponse.getMessage());
                                    } else {
                                        ViewUtil.showSnackbarMessage(mBinding,
                                                ErrorUtil.getGeneralExceptionMessage(getContext(),
                                                        response.code()));
                                    }
                                }
                            }));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

}
