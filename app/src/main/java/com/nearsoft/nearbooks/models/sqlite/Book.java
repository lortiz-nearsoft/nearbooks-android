package com.nearsoft.nearbooks.models.sqlite;

import android.databinding.Bindable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.nearsoft.nearbooks.BR;
import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Book sqlite model.
 * Created by epool on 12/17/15.
 */
@ModelContainer
@TableEndpoint(name = Book.NAME, contentProviderName = NearbooksDatabase.CONTENT_PROVIDER_NAME)
@Table(database = NearbooksDatabase.class)
public class Book extends NearbooksBaseObservableModel<Book> implements Parcelable {

    public static final String NAME = "Book";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE)
    public static final Uri CONTENT_URI = buildUri(NAME);
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    @Column
    @PrimaryKey
    @SerializedName("bookID")
    protected String id;
    @Column
    @SerializedName("title")
    protected String title;
    @Column
    @SerializedName("author")
    protected String author;
    @Column
    @SerializedName("releaseYear")
    protected int releaseYear;
    @Column
    @SerializedName("description")
    protected String description;
    @Column
    @SerializedName("copies")
    protected int numberOfCopies;
    @Column
    @SerializedName("days")
    protected int numberOfDaysAllowedForBorrowing;
    @Column
    @SerializedName("isAvailable")
    protected boolean isAvailable;
    @SerializedName("borrows")
    protected List<Borrow> borrows;

    public Book() {
    }

    protected Book(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.releaseYear = in.readInt();
        this.description = in.readString();
        this.numberOfCopies = in.readInt();
        this.numberOfDaysAllowedForBorrowing = in.readInt();
        this.isAvailable = in.readByte() != 0;
        this.borrows = in.createTypedArrayList(Borrow.CREATOR);
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
    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
        notifyPropertyChanged(BR.releaseYear);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
        notifyPropertyChanged(BR.numberOfCopies);
    }

    @Bindable
    public int getNumberOfDaysAllowedForBorrowing() {
        return numberOfDaysAllowedForBorrowing;
    }

    public void setNumberOfDaysAllowedForBorrowing(int numberOfDaysAllowedForBorrowing) {
        this.numberOfDaysAllowedForBorrowing = numberOfDaysAllowedForBorrowing;
        notifyPropertyChanged(BR.numberOfDaysAllowedForBorrowing);
    }

    @Bindable
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
        notifyPropertyChanged(BR.available);
    }

    @Bindable
    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "borrows")
    public List<Borrow> getBorrows() {
        if (borrows == null || borrows.isEmpty()) {
            borrows = SQLite.select()
                    .from(Borrow.class)
                    .where(Borrow_Table.bookForeignKeyContainer_id.eq(id))
                    .queryList();
        }
        return borrows;
    }

    public void setBorrows(List<Borrow> borrows) {
        this.borrows = borrows;
        notifyPropertyChanged(BR.borrows);
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

        if (releaseYear != book.releaseYear) return false;
        if (numberOfCopies != book.numberOfCopies) return false;
        if (numberOfDaysAllowedForBorrowing != book.numberOfDaysAllowedForBorrowing) return false;
        if (isAvailable != book.isAvailable) return false;
        if (id != null ? !id.equals(book.id) : book.id != null) return false;
        if (title != null ? !title.equals(book.title) : book.title != null) return false;
        if (author != null ? !author.equals(book.author) : book.author != null) return false;
        if (description != null ? !description.equals(book.description) : book.description != null)
            return false;
        return borrows != null ? borrows.equals(book.borrows) : book.borrows == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + releaseYear;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + numberOfCopies;
        result = 31 * result + numberOfDaysAllowedForBorrowing;
        result = 31 * result + (isAvailable ? 1 : 0);
        result = 31 * result + (borrows != null ? borrows.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeInt(this.releaseYear);
        dest.writeString(this.description);
        dest.writeInt(this.numberOfCopies);
        dest.writeInt(this.numberOfDaysAllowedForBorrowing);
        dest.writeByte(isAvailable ? (byte) 1 : (byte) 0);
        dest.writeTypedList(borrows);
    }
}
