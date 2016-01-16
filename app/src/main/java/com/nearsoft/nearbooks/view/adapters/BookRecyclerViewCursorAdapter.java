package com.nearsoft.nearbooks.view.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.util.ImageLoader;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;
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
        private BookItemBinding mBinding;

        public BookViewHolder(BookItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void setupViewAtPosition(int position) {
            Book book = mFlowCursorAdapter.getItem(position);

            final Context context = mBinding.getRoot().getContext();

            new ImageLoader.Builder(null, mBinding.imageViewBookCover,
                    context.getString(R.string.url_book_cover_thumbnail, book.getId()))
                    .placeholderResourceId(R.drawable.ic_launcher)
                    .errorResourceId(R.drawable.ic_launcher)
                    .paletteAsyncListener(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            int defaultColor = ContextCompat
                                    .getColor(context, R.color.colorPrimary);
                            ColorsWrapper colorsWrapper = ViewUtil
                                    .getVibrantPriorityColorSwatchPair(palette, defaultColor);
                            mBinding.setColors(colorsWrapper);
                            mBinding.executePendingBindings();
                        }
                    })
                    .load();

            mBinding.setBook(book);
            mBinding.executePendingBindings();
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
