package com.nearsoft.nearbooks.view.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityBookDetailBinding;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment;
import com.nearsoft.nearbooks.view.helpers.SimpleTransitionListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BookDetailActivity extends BaseActivity {

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_BOOK_COVER = "detail:book_cover:image";

    private ActivityBookDetailBinding mBinding;
    private Book mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = getBinding(ActivityBookDetailBinding.class);
        ViewCompat.setTransitionName(mBinding.imageViewBookCover, VIEW_NAME_BOOK_COVER);

        mBook = getIntent().getParcelableExtra(BookDetailFragment.ARG_BOOK_ITEM);
        mBinding.setBook(mBook);

        mBinding.fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        loadItem();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_book_detail;
    }

    private void loadItem() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            // If we're running on Lollipop and we have added a listener to the shared element
            // transition, load the thumbnail. The listener will load the full-size image when
            // the transition is complete.
            loadThumbnail();
        } else {
            // If all other cases we should just load the full-size image now
            loadFullSizeImage(true);
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
                    loadFullSizeImage(false);

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

    /**
     * Load the item's thumbnail image into our {@link ImageView}.
     */
    private void loadThumbnail() {
        Picasso.with(this)
                .load(getString(R.string.url_book_cover_thumbnail, mBook.getId()))
                .noFade()
                .into(mBinding.imageViewBookCover);
    }

    /**
     * Load the item's full-size image into our {@link ImageView}.
     */
    private void loadFullSizeImage(boolean loadThumbnailFirst) {
        String imageUrl = getString(
                loadThumbnailFirst ?
                        R.string.url_book_cover_thumbnail :
                        R.string.url_book_cover_full,
                mBook.getId());

        Picasso.with(BookDetailActivity.this)
                .load(imageUrl)
                .noFade()
                .noPlaceholder()
                .error(R.drawable.ic_launcher)
                .into(mBinding.imageViewBookCover, new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.with(BookDetailActivity.this)
                                .load(getString(R.string.url_book_cover_full, mBook.getId()))
                                .noFade()
                                .noPlaceholder()
                                .error(R.drawable.ic_launcher)
                                .into(mBinding.imageViewBookCover);
                    }

                    @Override
                    public void onError() {
                    }
                });
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
