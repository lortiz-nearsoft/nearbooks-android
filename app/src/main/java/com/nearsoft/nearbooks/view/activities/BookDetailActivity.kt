package com.nearsoft.nearbooks.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.MenuItem

import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.BookItemBinding
import com.nearsoft.nearbooks.di.components.BaseActivityComponent
import com.nearsoft.nearbooks.models.view.Book
import com.nearsoft.nearbooks.view.fragments.BookDetailFragment
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper

class BookDetailActivity : BaseActivity() {

    companion object {

        fun openWith(activity: Activity, binding: BookItemBinding) {
            val detailIntent = Intent(activity, BookDetailActivity::class.java)
            detailIntent.putExtra(BookDetailFragment.ARG_BOOK, binding.book as Parcelable?)
            detailIntent.putExtra(BookDetailFragment.ARG_COLORS_WRAPPER, binding.colors as Parcelable?)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    Pair.create(
                            binding.root.findViewById(R.id.imageViewBookCover),
                            BookDetailFragment.VIEW_NAME_BOOK_COVER),
                    Pair.create(
                            binding.root.findViewById(R.id.toolbar),
                            BookDetailFragment.VIEW_NAME_BOOK_TOOLBAR))
            ActivityCompat.startActivity(activity, detailIntent, options.toBundle())
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            val book = intent.getParcelableExtra<Book>(BookDetailFragment.ARG_BOOK)
            val colorsWrapper = intent.getParcelableExtra<ColorsWrapper>(BookDetailFragment.ARG_COLORS_WRAPPER)
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val noteDetailFragment = BookDetailFragment.newInstance(book, colorsWrapper)
            supportFragmentManager.beginTransaction().add(R.id.content, noteDetailFragment).commit()
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_book_detail
    }

    override fun injectComponent(baseActivityComponent: BaseActivityComponent) {
        super.injectComponent(baseActivityComponent)
        baseActivityComponent.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
