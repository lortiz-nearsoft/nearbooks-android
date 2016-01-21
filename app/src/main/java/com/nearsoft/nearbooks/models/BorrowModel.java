package com.nearsoft.nearbooks.models;

import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.Book_Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

/**
 * Model to handle borrow actions.
 * Created by epool on 1/7/16.
 */
public class BorrowModel {

    public static ForeignKeyContainer<Book> bookForeignKeyContainerFromBookId(String bookId) {
        Book book = new Book();
        book.setId(bookId);
        return FlowManager
                .getContainerAdapter(Book.class)
                .toForeignKeyContainer(book);
    }

    public static String bookIdFromBookForeignKeyContainer(ForeignKeyContainer<Book>
                                                                   bookForeignKeyContainer) {
        return bookForeignKeyContainer.getStringValue(Book_Table.id.getContainerKey());
    }

}
