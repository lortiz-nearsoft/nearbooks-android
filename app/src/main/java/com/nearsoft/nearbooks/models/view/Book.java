package com.nearsoft.nearbooks.models.view;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.nearsoft.nearbooks.BR;

import java.util.ArrayList;
import java.util.List;

/**
 * Book view model.
 * Created by epool on 5/1/16.
 */
public class Book extends BaseObservable implements Parcelable {

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private String id;
    private String title;
    private String author;
    private String releaseYear;
    private String description;
    private int numberOfCopies;
    private int numberOfDaysAllowedForBorrowing;
    private boolean isAvailable;
    private List<Borrow> borrows;

    public Book() {
    }

    public Book(com.nearsoft.nearbooks.models.realm.Book book) {
        id = book.getId();
        title = book.getTitle();
        author = book.getAuthor();
        releaseYear = book.getReleaseYear();
        description = book.getDescription();
        numberOfCopies = book.getNumberOfCopies();
        numberOfDaysAllowedForBorrowing = book.getNumberOfDaysAllowedForBorrowing();
        isAvailable = book.isAvailable();
        borrows = new ArrayList<>();
        for (com.nearsoft.nearbooks.models.realm.Borrow borrow : book.getBorrows()) {
            borrows.add(new Borrow(borrow));
        }
    }

    protected Book(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.releaseYear = in.readString();
        this.description = in.readString();
        this.numberOfCopies = in.readInt();
        this.numberOfDaysAllowedForBorrowing = in.readInt();
        this.isAvailable = in.readByte() != 0;
        this.borrows = new ArrayList<Borrow>();
        in.readList(this.borrows, Borrow.class.getClassLoader());
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
    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
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
    public List<Borrow> getBorrows() {
        return borrows;
    }

    public void setBorrows(List<Borrow> borrows) {
        this.borrows = borrows;
        notifyPropertyChanged(BR.borrows);
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
        dest.writeString(this.releaseYear);
        dest.writeString(this.description);
        dest.writeInt(this.numberOfCopies);
        dest.writeInt(this.numberOfDaysAllowedForBorrowing);
        dest.writeByte(isAvailable ? (byte) 1 : (byte) 0);
        dest.writeList(this.borrows);
    }
}
