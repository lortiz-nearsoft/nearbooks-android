package com.nearsoft.nearbooks.view.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nearsoft.nearbooks.BR;
import com.nearsoft.nearbooks.models.realm.Book;

/**
 * Book class.
 * Created by epool on 11/18/15.
 */
public class BookViewModel extends BaseObservable implements Parcelable {
    public static final Parcelable.Creator<BookViewModel> CREATOR = new Parcelable.Creator<BookViewModel>() {
        public BookViewModel createFromParcel(Parcel source) {
            return new BookViewModel(source);
        }

        public BookViewModel[] newArray(int size) {
            return new BookViewModel[size];
        }
    };
    @SerializedName("Id")
    private String id;
    @SerializedName("ISBN")
    private String isbn;
    @SerializedName("Title")
    private String title;
    @SerializedName("Author")
    private String author;
    @SerializedName("year")
    private String year;
    @SerializedName("IsAvailable")
    private boolean isAvailable;

    public BookViewModel() {
    }

    public BookViewModel(Book book) {
        this.id = book.getId();
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.year = book.getYear();
        this.isAvailable = book.getIsAvailable();
    }

    protected BookViewModel(Parcel in) {
        this.id = in.readString();
        this.isbn = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.year = in.readString();
        this.isAvailable = in.readByte() != 0;
    }

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
        notifyPropertyChanged(BR.isbn);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
        notifyPropertyChanged(BR.author);
    }

    @Bindable
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
        notifyPropertyChanged(BR.year);
    }

    @Bindable
    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
        notifyPropertyChanged(BR.isAvailable);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.isbn);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.year);
        dest.writeByte(isAvailable ? (byte) 1 : (byte) 0);
    }

    public Book toRealm() {
        Book book = new Book();
        book.setId(id);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setIsAvailable(isAvailable);
        return book;
    }
}
