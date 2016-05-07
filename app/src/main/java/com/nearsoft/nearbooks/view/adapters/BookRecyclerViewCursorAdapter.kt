package com.nearsoft.nearbooks.view.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.BookItemBinding
import com.nearsoft.nearbooks.models.BookModel
import com.nearsoft.nearbooks.models.realm.Book
import com.nearsoft.nearbooks.view.adapters.listeners.OnBookItemClickListener
import com.nearsoft.nearbooks.view.adapters.realm.RealmRecyclerViewAdapter
import com.nearsoft.nearbooks.view.adapters.viewholders.BookViewHolder
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper
import io.realm.Realm
import javax.inject.Inject

/**
 * Recycler view cursor adapter.
 * Created by epool on 12/17/15.
 */
class BookRecyclerViewCursorAdapter(
        context: Context,
        private val mRealm: Realm,
        private val mOnBookItemClickListener: OnBookItemClickListener
) : RealmRecyclerViewAdapter<Book>(context, BookModel.getAllBooks(mRealm)) {

    @Inject
    lateinit protected var defaultColors: ColorsWrapper

    init {
        NearbooksApplication.applicationComponent().inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = DataBindingUtil.inflate<BookItemBinding>(LayoutInflater.from(parent.context), R.layout.book_item, parent, false)
        return BookViewHolder(binding, defaultColors, mOnBookItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BookViewHolder) {
            holder.setupViewAtPosition(getItem(position))
        }
    }

    fun filterByQuery(query: String) {
        val booksByQuery = BookModel.getBooksByQuery(mRealm, query)
        updateData(booksByQuery)
        notifyDataSetChanged()
    }

}
