package com.nearsoft.nearbooks.view.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.BookItemBinding
import com.nearsoft.nearbooks.models.realm.Book
import com.nearsoft.nearbooks.util.ViewUtil
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper

/**
 * Book view holder.
 * Created by epool on 5/6/16.
 */
class BookViewHolder(
        private val mBinding: BookItemBinding,
        private val defaultColors: ColorsWrapper,
        private val mOnBookItemClickListener: (BookItemBinding) -> Unit
) : RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {

    init {
        mBinding.root.setOnClickListener(this)
        mBinding.toolbar.setOnClickListener(this)
    }

    fun setupViewAtPosition(realmBook: Book) {
        val book = com.nearsoft.nearbooks.models.view.Book(realmBook)

        mBinding.setBook(book)
        mBinding.setColors(defaultColors)
        mBinding.executePendingBindings()

        val context = mBinding.root.context

        ViewUtil.loadImageFromUrl(mBinding.imageViewBookCover,
                context.getString(R.string.url_book_cover_thumbnail, book.id)).subscribe { colorsWrapper ->
            mBinding.colors = colorsWrapper
            mBinding.executePendingBindings()
        }
    }

    override fun onClick(v: View) {
        mOnBookItemClickListener(mBinding)
    }
}