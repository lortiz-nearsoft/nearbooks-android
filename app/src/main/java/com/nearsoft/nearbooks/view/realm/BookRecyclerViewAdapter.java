package com.nearsoft.nearbooks.view.realm;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.view.models.BookViewModel;

import io.realm.RealmResults;

/**
 * Book recycler view adapter.
 * Created by epool on 11/20/15.
 */
public class BookRecyclerViewAdapter extends RealmRecyclerViewAdapter<Book> {

    public BookRecyclerViewAdapter(Context context, RealmResults<Book> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BookItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.book_item, parent, false);
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Book book = getItem(position);
        BookItemBinding binding = ((BookViewHolder) holder).binding;
        binding.setBook(new BookViewModel(book));
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        private BookItemBinding binding;

        public BookViewHolder(BookItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
