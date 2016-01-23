package com.nearsoft.nearbooks.view.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityBookDetailBinding;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;
import com.nearsoft.nearbooks.view.helpers.SimpleTransitionListener;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;
import com.squareup.picasso.Picasso;

import retrofit2.Response;
import rx.Subscriber;

public class BookDetailActivity extends BaseActivity {

    public static final String ARG_COLORS_WRAPPER = "PARAM_COLORS_WRAPPER";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_BOOK_COVER = "detail:book_cover:image";
    public static final String VIEW_NAME_BOOK_TOOLBAR = "detail:book_toolbar:toolbar";

    private ActivityBookDetailBinding mBinding;
    private Book mBook;

    public static void openWith(Activity activity, BookItemBinding binding) {
        Intent detailIntent = new Intent(activity, BookDetailActivity.class);
        detailIntent.putExtra(BookDetailFragment.ARG_BOOK_ITEM, binding.getBook());
        detailIntent.putExtra(ARG_COLORS_WRAPPER, binding.getColors());

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        activity,
                        Pair.create(
                                binding.getRoot().findViewById(R.id.imageViewBookCover),
                                VIEW_NAME_BOOK_COVER
                        ),
                        Pair.create(
                                binding.getRoot().findViewById(R.id.toolbar),
                                VIEW_NAME_BOOK_TOOLBAR
                        )
                );
        ActivityCompat.startActivity(activity, detailIntent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = getBinding(ActivityBookDetailBinding.class);
        mBook = getIntent().getParcelableExtra(BookDetailFragment.ARG_BOOK_ITEM);
        final ColorsWrapper colorsWrapper = getIntent().getParcelableExtra(ARG_COLORS_WRAPPER);
        mBinding.setBook(mBook);
        mBinding.setColors(colorsWrapper);
        mBinding.toolbar.post(new Runnable() {
            @Override
            public void run() {
                boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);
                if (isLandscape && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(colorsWrapper.getBackgroundColor());
                }
                ViewUtil.Toolbar.colorizeToolbar(mBinding.toolbar,
                        colorsWrapper.getTitleTextColor(), BookDetailActivity.this);
            }
        });

        ViewCompat.setTransitionName(mBinding.imageViewBookCover, VIEW_NAME_BOOK_COVER);
        ViewCompat.setTransitionName(mBinding.toolbar, VIEW_NAME_BOOK_TOOLBAR);

        mBinding.fabRequestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeToActivity(BookModel.requestBookToBorrow(mBinding, mLazyUser.get(),
                        mBook.getId() + "-0", mBinding.fabRequestBook));
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            BookDetailFragment noteDetailFragment = BookDetailFragment.newInstance(mBook);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.note_detail_container, noteDetailFragment)
                    .commit();
        }

        Picasso.with(this)
                .load(getString(R.string.url_book_cover_thumbnail, mBook.getId()))
                .noPlaceholder()
                .noFade()
                .error(R.drawable.ic_launcher)
                .into(mBinding.imageViewBookCover);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || !addTransitionListener()) {
            loadFullResolution();
            checkBookAvailability();
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
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

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
        subscribeToActivity(BookModel.checkBookAvailability(mBook)
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

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected void injectComponent(BaseActivityComponent baseActivityComponent) {
        super.injectComponent(baseActivityComponent);
        baseActivityComponent.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
