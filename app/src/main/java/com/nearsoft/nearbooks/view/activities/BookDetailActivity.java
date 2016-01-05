package com.nearsoft.nearbooks.view.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityBookDetailBinding;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment;

public class BookDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityBookDetailBinding binding = getBinding(ActivityBookDetailBinding.class);

        Book book = getIntent().getParcelableExtra(BookDetailFragment.ARG_BOOK_ITEM);
        binding.setBook(book);

        binding.fabEdit.setOnClickListener(new View.OnClickListener() {
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
            BookDetailFragment noteDetailFragment = BookDetailFragment.newInstance(book);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.note_detail_container, noteDetailFragment)
                    .commit();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected int getToolbarId() {
        return R.id.detail_toolbar;
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
