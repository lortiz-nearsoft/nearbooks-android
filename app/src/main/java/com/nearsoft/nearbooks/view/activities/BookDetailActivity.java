package com.nearsoft.nearbooks.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityBookDetailBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.util.ImageLoader;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;
import com.nearsoft.nearbooks.ws.responses.AvailabilityResponse;

import retrofit2.Call;
import retrofit2.Response;
import rx.Subscriber;

public class BookDetailActivity extends BaseActivity {

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_BOOK_COVER = "detail:book_cover:image";
    public static final String VIEW_NAME_BOOK_TOOLBAR = "detail:book_toolbar:toolbar";

    private ActivityBookDetailBinding mBinding;
    private Book mBook;
    private Call<AvailabilityResponse> mCall;

    public static void openWith(Activity activity, Book book, View view) {
        Intent detailIntent = new Intent(activity, BookDetailActivity.class);
        detailIntent.putExtra(BookDetailFragment.ARG_BOOK_ITEM, book);

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        activity,
                        Pair.create(
                                view.findViewById(R.id.imageViewBookCover),
                                VIEW_NAME_BOOK_COVER
                        ),
                        Pair.create(
                                view.findViewById(R.id.toolbar),
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
        mBinding.setBook(mBook);

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

        new ImageLoader.Builder(this, mBinding.imageViewBookCover,
                getString(R.string.url_book_cover_thumbnail, mBook.getId()))
                .fullResolutionImageUrl(getString(R.string.url_book_cover_full, mBook.getId()))
                .placeholderResourceId(R.drawable.ic_launcher)
                .errorResourceId(R.drawable.ic_launcher)
                .paletteAsyncListener(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int defaultColor = ContextCompat
                                .getColor(BookDetailActivity.this, R.color.colorPrimary);
                        ColorsWrapper colorsWrapper = ViewUtil
                                .getVibrantPriorityColorSwatchPair(palette, defaultColor);
                        mBinding.setColors(colorsWrapper);
                        ViewUtil.Toolbar.colorizeToolbar(mBinding.toolbar, colorsWrapper
                                .getTitleTextColor(), BookDetailActivity.this);

                        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);
                        if (isLandscape && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setStatusBarColor(palette
                                    .getDarkVibrantColor(defaultColor));
                        }

                        checkBookAvailability();
                    }
                })
                .load();
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
    protected void onDestroy() {
        if (mCall != null) mCall.cancel();

        super.onDestroy();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_book_detail;
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
