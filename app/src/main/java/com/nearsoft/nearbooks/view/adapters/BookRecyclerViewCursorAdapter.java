package com.nearsoft.nearbooks.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.Where;

import javax.inject.Inject;

/**
 * Recycler view cursor adapter.
 * Created by epool on 12/17/15.
 */
public class BookRecyclerViewCursorAdapter
        extends RecyclerView.Adapter<BookRecyclerViewCursorAdapter.BookViewHolder>
        implements Filterable {

    @Inject
    protected ColorsWrapper defaultColors;
    private FlowCursorList<Book> mFlowCursorAdapter;
    private BookFilter mBookFilter = new BookFilter();
    private OnBookItemClickListener mOnBookItemClickListener;

    public BookRecyclerViewCursorAdapter(Where<Book> bookWhere,
                                         OnBookItemClickListener onBookItemClickListener) {
        NearbooksApplication.getNearbooksApplicationComponent().inject(this);
        mFlowCursorAdapter = new FlowCursorList<>(false, bookWhere);
        mOnBookItemClickListener = onBookItemClickListener;
    }

    public void updateCondition(Where<Book> bookWhere) {
        mFlowCursorAdapter = new FlowCursorList<>(false, bookWhere);
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        mFlowCursorAdapter.refresh();
        notifyDataSetChanged();
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BookItemBinding binding = BookItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        holder.setupViewAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return mFlowCursorAdapter.getCount();
    }

    public Book getItem(int position) {
        return mFlowCursorAdapter.getItem(position);
    }

    @Override
    public Filter getFilter() {
        return mBookFilter;
    }

    public interface OnBookItemClickListener {

        void onBookItemClicked(BookItemBinding binding);

    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private BookItemBinding mBinding;

        public BookViewHolder(BookItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener(this);
            mBinding.toolbar.setOnClickListener(this);
        }

        public void setupViewAtPosition(int position) {
            final Book book = mFlowCursorAdapter.getItem(position);

            mBinding.setBook(book);
            mBinding.setColors(defaultColors);
            mBinding.executePendingBindings();

            final Context context = mBinding.getRoot().getContext();

            ViewUtil.loadImageFromUrl(mBinding.imageViewBookCover,
                    context.getString(R.string.url_book_cover_thumbnail, book.getId()))
                    .subscribe(colorsWrapper -> {
                        mBinding.setColors(colorsWrapper);
                        mBinding.executePendingBindings();
                    });
        }

        @Override
        public void onClick(View v) {
            if (mOnBookItemClickListener != null) {
                mOnBookItemClickListener.onBookItemClicked(mBinding);
            }
        }
    }

    private class BookFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            filterResults.values = BookModel.getBooksByQuery(constraint);

            return filterResults;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Where<Book> bookWhere = (Where<Book>) results.values;
            updateCondition(bookWhere);
        }

    }

}
