package com.nearsoft.nearbooks.models;

import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Book_Table;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

/**
 * Model to handle book actions.
 * Created by epool on 11/18/15.
 */
public class BookModel {

    public static void cacheBooks(final List<Book> books) {
        if (books == null || books.isEmpty()) return;

        Delete.table(Book.class);

        TransactionManager.transact(NearbooksDatabase.NAME, new Runnable() {
            @Override
            public void run() {
                for (Book book : books) {
                    book.save();
                }
            }
        });
    }

    public static Book findByIsbn(String isbn) {
        return SQLite
                .select()
                .from(Book.class)
                .where(Book_Table.isbn.eq(isbn))
                .querySingle();
    }

    public static Where<Book> getAllBooks() {
        return SQLite
                .select()
                .from(Book.class)
                .orderBy(Book_Table.title, true);
    }

}
