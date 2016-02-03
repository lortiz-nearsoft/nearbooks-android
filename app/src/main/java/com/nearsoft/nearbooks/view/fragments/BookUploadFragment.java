package com.nearsoft.nearbooks.view.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentUploadBookBinding;
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

import retrofit2.Response;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookUploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookUploadFragment extends BaseFragment
        implements GoogleBooksVolumeAdapter.OnGoogleBookItemClickListener {

    private final static String KEY_CODE = "KEY_CODE";

    private FragmentUploadBookBinding mBinding;
    private GoogleBooksVolumeAdapter mGoogleBooksVolumeAdapter;
    private String mCode;

    public BookUploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookUploadFragment.
     */
    public static BookUploadFragment newInstance() {
        BookUploadFragment fragment = new BookUploadFragment();
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
        mBinding = FragmentUploadBookBinding
                .inflate(inflater, container, false);
        mBinding.fabScanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRScanner();
            }
        });

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
        subscribeToFragment(GoogleBooksModel.findGoogleBooksByIsbn(mCode)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable t) {
                        ViewUtil.showSnackbarMessage(mBinding, t.getLocalizedMessage());
                    }
                })
                .subscribe(new Action1<List<GoogleBookBody>>() {
                    @Override
                    public void call(List<GoogleBookBody> googleBookBodies) {
                        mGoogleBooksVolumeAdapter.setGoogleBookBodies(googleBookBodies);
                    }
                }));
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt(getString(R.string.message_scan_book_qr_code));
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
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subscribeToFragment(BookModel.registerNewBook(googleBookBody)
                                .doOnError(new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable t) {
                                        ViewUtil.showSnackbarMessage(mBinding,
                                                t.getLocalizedMessage());
                                    }
                                })
                                .subscribe(new Action1<Response<MessageResponse>>() {
                                    @Override
                                    public void call(
                                            Response<MessageResponse> response) {
                                        if (response.isSuccess()) {
                                            Toast.makeText(getContext(), "Done!", Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            MessageResponse messageResponse = ErrorUtil
                                                    .parseError(MessageResponse.class, response);
                                            if (messageResponse != null) {
                                                ViewUtil.showSnackbarMessage(mBinding,
                                                        messageResponse.getMessage());
                                            } else {
                                                ViewUtil.showSnackbarMessage(mBinding,
                                                        getString(R.string.error_general,
                                                                String.valueOf(response.code())));
                                            }
                                        }
                                    }
                                }));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

}
