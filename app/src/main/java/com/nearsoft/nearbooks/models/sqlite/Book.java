package com.nearsoft.nearbooks.models.sqlite;

import android.databinding.Bindable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nearsoft.nearbooks.BR;
import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Book sqlite model.
 * Created by epool on 12/17/15.
 */
@TableEndpoint(name = Book.NAME, contentProviderName = NearbooksDatabase.CONTENT_PROVIDER_NAME)
@Table(database = NearbooksDatabase.class)
public class Book extends NearbooksBaseObservableModel<Book> implements Parcelable {

    public static final String NAME = "Book";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE)
    public static final Uri CONTENT_URI = buildUri(NAME);

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Column
    @PrimaryKey
    @SerializedName("Id")
    private String id;

    @Column
    @SerializedName("ISBN")
    private String isbn;

    @Column
    @SerializedName("Title")
    private String title;

    @Column
    @SerializedName("Author")
    private String author;

    @Column
    @SerializedName("year")
    private String year;

    @Column
    @SerializedName("IsAvailable")
    private boolean isAvailable;

    public Book() {
    }

    protected Book(Parcel in) {
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
    public Uri getDeleteUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return CONTENT_URI;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (isAvailable != book.isAvailable) return false;
        if (id != null ? !id.equals(book.id) : book.id != null) return false;
        if (isbn != null ? !isbn.equals(book.isbn) : book.isbn != null) return false;
        if (title != null ? !title.equals(book.title) : book.title != null) return false;
        if (author != null ? !author.equals(book.author) : book.author != null) return false;
        return !(year != null ? !year.equals(book.year) : book.year != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (isAvailable ? 1 : 0);
        return result;
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

}
