package com.nearsoft.nearbooks.models;

import com.nearsoft.nearbooks.models.realm.Book;
import com.nearsoft.nearbooks.models.viewmodels.BookViewModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Model to handle book actions.
 * Created by epool on 11/18/15.
 */
public class BookModel {

    public static void cacheBooks(List<BookViewModel> books) {
        clearBooks();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        for (BookViewModel bookViewModel : books) {
            realm.copyToRealmOrUpdate(bookViewModel.toRealm());
        }

        realm.commitTransaction();

        realm.close();
    }

    public static RealmResults<Book> getAllBooks(Realm realm) {
        return realm.where(Book.class).findAllSorted(Book.TITLE, true);
    }

    public static void clearBooks() {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.where(Book.class).findAll().clear();
        realm.commitTransaction();

        realm.close();
    }

}
