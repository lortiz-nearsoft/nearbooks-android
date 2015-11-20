package com.nearsoft.nearbooks.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Book class.
 * Created by epool on 11/18/15.
 */
public class Book extends RealmObject {
    public static final String ISBN = "isbn";
    public static final String TITLE = "title";

    @PrimaryKey
    private String id;
    @Required
    private String isbn;
    @Required
    private String title;
    @Required
    private String author;
    @Required
    private String year;
    private boolean isAvailable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
