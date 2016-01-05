package com.nearsoft.nearbooks.view.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.Where;

/**
 * Recycler view cursor adapter.
 * Created by epool on 12/17/15.
 */
public class BookRecyclerViewCursorAdapter
        extends RecyclerView.Adapter<BookRecyclerViewCursorAdapter.BookViewHolder>
        implements Filterable {

    private FlowCursorList<Book> mFlowCursorAdapter;
    private BookFilter mBookFilter = new BookFilter();

    public BookRecyclerViewCursorAdapter(Where<Book> bookWhere) {
        mFlowCursorAdapter = new FlowCursorList<>(false, bookWhere);
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
        BookItemBinding binding = DataBindingUtil
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.book_item, parent, false
                );
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

    public class BookViewHolder extends RecyclerView.ViewHolder {
        private BookItemBinding binding;

        public BookViewHolder(BookItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setupViewAtPosition(int position) {
            Book book = mFlowCursorAdapter.getItem(position);
            binding.setBook(book);
            binding.executePendingBindings();
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
