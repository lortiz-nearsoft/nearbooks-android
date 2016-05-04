package com.nearsoft.nearbooks.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.util.ViewUtil;
import com.nearsoft.nearbooks.view.adapters.realm.RealmRecyclerViewAdapter;
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Recycler view cursor adapter.
 * Created by epool on 12/17/15.
 */
public class BookRecyclerViewCursorAdapter
        extends RealmRecyclerViewAdapter<Book> {

    @Inject
    protected ColorsWrapper defaultColors;
    private OnBookItemClickListener mOnBookItemClickListener;
    private Realm mRealm;

    public BookRecyclerViewCursorAdapter(Context context,
                                         Realm realm,
                                         OnBookItemClickListener onBookItemClickListener) {
        super(context, BookModel.INSTANCE.getAllBooks(realm));
        mRealm = realm;
        NearbooksApplication.Companion.applicationComponent().inject(this);
        mOnBookItemClickListener = onBookItemClickListener;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BookItemBinding binding = BookItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookViewHolder) {
            BookViewHolder headerViewHolder = (BookViewHolder) holder;
            headerViewHolder.setupViewAtPosition(position);
        }
    }

    public void filterByQuery(String query) {
        RealmResults<Book> booksByQuery = BookModel.INSTANCE.getBooksByQuery(mRealm, query);
        updateData(booksByQuery);
        notifyDataSetChanged();
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
            final com.nearsoft.nearbooks.models.view.Book book = new com.nearsoft.nearbooks.models.view.Book(getItem(position));

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

}
