package com.nearsoft.nearbooks.view.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.FragmentBookDetailBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Borrow;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;
import com.nearsoft.nearbooks.view.helpers.SimpleTransitionListener;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.nearsoft.nearbooks.ws.responses.MessageResponse;
import com.squareup.picasso.Picasso;

import retrofit2.Response;
import rx.Subscriber;

public class BookDetailFragment extends BaseFragment {

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_BOOK_COVER = "detail:book_cover:image";
    public static final String VIEW_NAME_BOOK_TOOLBAR = "detail:book_toolbar:toolbar";

    public static final String ARG_BOOK = "ARG_BOOK";

    public static final String ARG_COLORS_WRAPPER = "ARG_COLORS_WRAPPER";

    private FragmentBookDetailBinding mBinding;

    private Book mBook;
    private ColorsWrapper mColorsWrapper;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    public static BookDetailFragment newInstance(Book book, ColorsWrapper colorsWrapper) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_BOOK, book);
        args.putParcelable(ARG_COLORS_WRAPPER, colorsWrapper);

        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_BOOK) &&
                getArguments().containsKey(ARG_COLORS_WRAPPER)) {
            // Load the dummy title specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load title from a title provider.
            mBook = getArguments().getParcelable(ARG_BOOK);
            mColorsWrapper = getArguments().getParcelable(ARG_COLORS_WRAPPER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentBookDetailBinding
                .inflate(inflater, container, false);
        mBinding.setBook(mBook);
        mBinding.setColors(mColorsWrapper);

        setupActionBar(mBinding.toolbar);

        mBinding.toolbar.post(() -> {
            boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);
            if (isLandscape && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getBaseActivity()
                        .getWindow().setStatusBarColor(mColorsWrapper.getBackgroundColor());
            }
            ViewUtil.Toolbar.colorizeToolbar(mBinding.toolbar,
                    mColorsWrapper.getTitleTextColor(), getBaseActivity());
        });

        ViewCompat.setTransitionName(mBinding.imageViewBookCover, VIEW_NAME_BOOK_COVER);
        ViewCompat.setTransitionName(mBinding.toolbar, VIEW_NAME_BOOK_TOOLBAR);

        mBinding.fabRequestBook.setOnClickListener(view -> subscribeToFragment(BookModel.requestBookToBorrow(mLazyUser.get(),
                mBook.getId() + "-0")
                .subscribe(new Subscriber<Response<Borrow>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable t) {
                        ViewUtil.showSnackbarMessage(mBinding, t.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Response<Borrow> response) {
                        if (response.isSuccess()) {
                            Borrow borrow = response.body();
                            mBinding.setBorrow(borrow);
                            mBinding.executePendingBindings();
                            switch (borrow.getStatus()) {
                                case Borrow.STATUS_REQUESTED:
                                    ViewUtil.showSnackbarMessage(mBinding,
                                            getString(R.string.message_book_requested));
                                    break;
                                case Borrow.STATUS_ACTIVE:
                                    ViewUtil.showSnackbarMessage(mBinding,
                                            getString(R.string.message_book_active));
                                    break;
                                case Borrow.STATUS_CANCELLED:
                                case Borrow.STATUS_COMPLETED:
                                default:
                                    break;
                            }
                            mBinding.fabRequestBook.hide();
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
                    }
                })));

        Picasso.with(getContext())
                .load(getString(R.string.url_book_cover_thumbnail, mBook.getId()))
                .noPlaceholder()
                .noFade()
                .error(R.drawable.ic_launcher)
                .into(mBinding.imageViewBookCover);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ||
                !addTransitionListener(getBaseActivity())) {
            loadFullResolution();
            checkBookAvailability();
        }

        return mBinding.getRoot();
    }

    private void setupActionBar(Toolbar toolbar) {
        // Show the Up button in the action bar.
        getBaseActivity().setSupportActionBar(toolbar);
        ActionBar actionBar = getBaseActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener(final BaseActivity baseActivity) {
        final Transition transition = baseActivity.getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    loadFullResolution();
                    checkBookAvailability();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }

    private void loadFullResolution() {
        Picasso.with(mBinding.imageViewBookCover.getContext())
                .load(getString(R.string.url_book_cover_full, mBook.getId()))
                .noFade()
                .noPlaceholder()
                .error(R.drawable.ic_launcher)
                .into(mBinding.imageViewBookCover);
    }

    private void checkBookAvailability() {
        subscribeToFragment(BookModel.checkBookAvailability(mBook)
                .subscribe(new Subscriber<Response<AvailabilityResponse>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable t) {
                        ViewUtil.showSnackbarMessage(mBinding, t.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Response<AvailabilityResponse> response) {
                        if (response.isSuccess()) {
                            AvailabilityResponse availabilityResponse = response.body();
                            mBinding.setBorrow(availabilityResponse.getActiveBorrrow());
                            mBinding.executePendingBindings();
                            if (availabilityResponse.isAvailable()) {
                                mBinding.fabRequestBook.show();
                            } else {
                                mBinding.fabRequestBook.hide();
                            }
                        }
                    }
                }));
    }

}
