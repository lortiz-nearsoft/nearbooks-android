package com.nearsoft.nearbooks.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.MenuItem;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.models.view.Book;
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;

public class BookDetailActivity extends BaseActivity {

    public static void openWith(Activity activity, BookItemBinding binding) {
        Intent detailIntent = new Intent(activity, BookDetailActivity.class);
        detailIntent.putExtra(BookDetailFragment.ARG_BOOK, binding.getBook());
        detailIntent.putExtra(BookDetailFragment.ARG_COLORS_WRAPPER, binding.getColors());

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        activity,
                        Pair.create(
                                binding.getRoot().findViewById(R.id.imageViewBookCover),
                                BookDetailFragment.VIEW_NAME_BOOK_COVER
                        ),
                        Pair.create(
                                binding.getRoot().findViewById(R.id.toolbar),
                                BookDetailFragment.VIEW_NAME_BOOK_TOOLBAR
                        )
                );
        ActivityCompat.startActivity(activity, detailIntent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Book book = getIntent().getParcelableExtra(BookDetailFragment.ARG_BOOK);
            ColorsWrapper colorsWrapper = getIntent()
                    .getParcelableExtra(BookDetailFragment.ARG_COLORS_WRAPPER);
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            BookDetailFragment noteDetailFragment = BookDetailFragment
                    .newInstance(book, colorsWrapper);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, noteDetailFragment)
                    .commit();
        }
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
