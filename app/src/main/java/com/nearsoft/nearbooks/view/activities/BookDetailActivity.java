package com.nearsoft.nearbooks.view.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityBookDetailBinding;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.util.ImageLoader;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment;

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
                        Pair<Integer, Palette.Swatch> pair = ViewUtil
                                .getVibrantPriorityColorSwatchPair(palette, defaultColor);
                        Palette.Swatch swatch = pair.second;
                        mBinding.toolbar.setBackgroundColor(pair.first);
                        mBinding.toolbarLayout
                                .setStatusBarScrimColor(swatch.getTitleTextColor());
                        mBinding.toolbarLayout.setContentScrimColor(pair.first);
                        ViewUtil.Toolbar.colorizeToolbar(mBinding.toolbar, swatch
                                .getTitleTextColor(), BookDetailActivity.this);
                        mBinding.textViewBookTitle.setTextColor(swatch.getTitleTextColor());
                        mBinding.textViewBookISBN.setTextColor(swatch.getBodyTextColor());
                        mBinding.textViewBookYear.setTextColor(swatch.getBodyTextColor());
                    }
                })
                .load();
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
