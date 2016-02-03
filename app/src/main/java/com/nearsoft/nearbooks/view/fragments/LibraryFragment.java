package com.nearsoft.nearbooks.view.fragments;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentLibraryBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.SyncChangeHandler;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.util.SyncUtil;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.nearsoft.nearbooks.view.activities.zxing.CaptureActivityAnyOrientation;
import com.nearsoft.nearbooks.view.adapters.BookRecyclerViewCursorAdapter;
import com.nearsoft.nearbooks.view.helpers.SpacingDecoration;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;

import retrofit2.Response;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookRecyclerViewCursorAdapter.OnBookItemClickListener} interface
 * to handle interaction events.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment
        extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener,
        SyncChangeHandler.OnSyncChangeListener,
        BaseActivity.OnSearchListener,
        Filter.FilterListener {

    private final static int ACTION_REQUEST = 0;
    private final static int ACTION_CHECK_IN = 1;
    private final static int ACTION_CHECK_OUT = 2;

    private BookRecyclerViewCursorAdapter mBookRecyclerViewCursorAdapter;
    private BookRecyclerViewCursorAdapter.OnBookItemClickListener mOnBookItemClickListener;
    private FragmentLibraryBinding mBinding;
    private SearchView mSearchView;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibraryFragment.
     */
    public static LibraryFragment newInstance() {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mBookRecyclerViewCursorAdapter =
                new BookRecyclerViewCursorAdapter(BookModel.getAllBooks(), mOnBookItemClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScanner();
            }
        });

        mBinding.recyclerViewBooks.setHasFixedSize(true);
        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);
        boolean isTablet = getResources().getBoolean(R.bool.isTable);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),
                isTablet ? isLandscape ? 6 : 4 : isLandscape ? 4 : 2);
        mBinding.recyclerViewBooks.setLayoutManager(layoutManager);
        int margin = getResources().getDimensionPixelSize(R.dimen.books_margin);
        mBinding.recyclerViewBooks.addItemDecoration(new SpacingDecoration(margin, margin, true));
        mBinding.recyclerViewBooks.setAdapter(mBookRecyclerViewCursorAdapter);

        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
        mBinding.textViewEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator
                .parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && scanResult.getContents() != null) {
            String qrCode = scanResult.getContents();
            showBookActions(qrCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnBookItemClickListener =
                    (BookRecyclerViewCursorAdapter.OnBookItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement " + mOnBookItemClickListener.getClass().getSimpleName());
        }

        BaseActivity baseActivity = getBaseActivity();
        SyncChangeHandler syncChangeHandler = baseActivity.getSyncChangeHandler();
        syncChangeHandler.addOnSyncChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnBookItemClickListener = null;

        getBaseActivity()
                .getSyncChangeHandler()
                .removeOnSyncChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the options menu from XML
        inflater.inflate(R.menu.menu_library, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager)
                getContext().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        // Assumes current activity is the searchable activity
        mSearchView
                .setSearchableInfo(
                        searchManager
                                .getSearchableInfo(
                                        getBaseActivity().getComponentName()
                                )
                );
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mSearchView.setIconified(true);
                    mSearchView.setQuery("", false);
                }
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mBookRecyclerViewCursorAdapter.getFilter().filter(query, LibraryFragment.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mBookRecyclerViewCursorAdapter.getFilter().filter(newText, LibraryFragment.this);
                return false;
            }
        });
    }

    @Override
    public void onRefresh() {
        User user = mLazyUser.get();
        if (!SyncUtil.isSyncing(user)) {
            SyncUtil.triggerRefresh(user);
        }
    }

    @Override
    public void onSyncChange(final boolean isSyncing) {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            mBinding.swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (!mBinding.swipeRefreshLayout.isRefreshing() && isSyncing) {
                        mBinding.swipeRefreshLayout.setRefreshing(true);
                    } else if (mBinding.swipeRefreshLayout.isRefreshing() && !isSyncing) {
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                    updateUI();
                }
            });
        }
    }

    private void updateUI() {
        if (mBookRecyclerViewCursorAdapter.getItemCount() == 0) {
            mBinding.recyclerViewBooks.setVisibility(View.GONE);
            mBinding.textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            mBinding.recyclerViewBooks.setVisibility(View.VISIBLE);
            mBinding.textViewEmpty.setVisibility(View.GONE);
        }
        mBookRecyclerViewCursorAdapter.notifyDataChanged();
    }

    @Override
    public void onSearchRequest(String query) {
        if (mSearchView != null) mSearchView.setQuery(query, false);
    }

    @Override
    public void onFilterComplete(int count) {
        updateUI();
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getString(R.string.message_scan_book_qr_code));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void showBookActions(@NonNull final String qrCode) {
        String[] qrCodeParts = qrCode.split("-");
        if (qrCodeParts.length == 2) {
            Book book = BookModel.findByBookId(qrCodeParts[0]);
            if (book != null) {
                new AlertDialog.Builder(getContext())
                        .setTitle(book.getTitle())
                        .setItems(R.array.actions_book, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case ACTION_REQUEST:
                                        subscribeToFragment(BookModel.requestBookToBorrow(
                                                mLazyUser.get(), qrCode)
                                                .subscribe(new Subscriber<Response<Borrow>>() {
                                                    @Override
                                                    public void onCompleted() {
                                                    }

                                                    @Override
                                                    public void onError(Throwable t) {
                                                        ViewUtil.showSnackbarMessage(mBinding,
                                                                t.getLocalizedMessage());
                                                    }

                                                    @Override
                                                    public void onNext(Response<Borrow> response) {
                                                        if (response.isSuccess()) {
                                                            Borrow borrow = response.body();
                                                            switch (borrow.getStatus()) {
                                                                case Borrow.STATUS_REQUESTED:
                                                                    ViewUtil.showSnackbarMessage(
                                                                            mBinding,
                                                                            getString(R.string.message_book_requested));
                                                                    break;
                                                                case Borrow.STATUS_ACTIVE:
                                                                    ViewUtil.showSnackbarMessage(
                                                                            mBinding,
                                                                            getString(R.string.message_book_active));
                                                                    break;
                                                                case Borrow.STATUS_CANCELLED:
                                                                case Borrow.STATUS_COMPLETED:
                                                                default:
                                                                    break;
                                                            }
                                                        } else {
                                                            MessageResponse messageResponse = ErrorUtil
                                                                    .parseError(MessageResponse.class, response);
                                                            if (messageResponse != null) {
                                                                ViewUtil.showSnackbarMessage(
                                                                        mBinding,
                                                                        messageResponse
                                                                                .getMessage());
                                                            } else {
                                                                ViewUtil.showSnackbarMessage(
                                                                        mBinding,
                                                                        getString(R.string.error_general,
                                                                                String.valueOf(response.code())));
                                                            }
                                                        }
                                                    }
                                                }));
                                        break;
                                    case ACTION_CHECK_IN:
                                        subscribeToFragment(BookModel.doBookCheckIn(mBinding,
                                                mLazyUser.get(), qrCode));
                                        break;
                                    case ACTION_CHECK_OUT:
                                        subscribeToFragment(BookModel.doBookCheckOut(mBinding,
                                                mLazyUser.get(), qrCode));
                                        break;
                                }
                            }
                        })
                        .show();
            } else {
                Snackbar
                        .make(
                                mBinding.getRoot(),
                                getResources()
                                        .getQuantityString(R.plurals.message_books_not_found, 1),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
            }
        } else {
            Snackbar
                    .make(
                            mBinding.getRoot(),
                            getString(R.string.error_invalid_qr_code),
                            Snackbar.LENGTH_LONG
                    )
                    .show();
        }
    }

}
