package com.nearsoft.nearbooks.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.View;
import android.view.ViewGroup;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.BookItemBinding;
import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.models.viewmodels.BookViewModel;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Book list adapter.
 * Created by epool on 11/18/15.
 */
public class BookListAdapter extends RealmBaseAdapter<Book> {


    public BookListAdapter(Context context, RealmResults<Book> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            BookItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.book_item, null, false);
            rowView = binding.getRoot();
            rowView.setTag(binding);
        }

        Book book = realmResults.get(position);

        BookItemBinding binding = (BookItemBinding) rowView.getTag();
        binding.setBook(new BookViewModel(book));

        return rowView;
    }
}
