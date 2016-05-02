package com.nearsoft.nearbooks.models.view;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.nearsoft.nearbooks.BR;
import com.nearsoft.nearbooks.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Borrow view model.
 * Created by epool on 5/1/16.
 */
public class Borrow extends BaseObservable implements Parcelable {

    public static final Parcelable.Creator<Borrow> CREATOR = new Parcelable.Creator<Borrow>() {
        @Override
        public Borrow createFromParcel(Parcel source) {
            return new Borrow(source);
        }

        @Override
        public Borrow[] newArray(int size) {
            return new Borrow[size];
        }
    };

    public static final int STATUS_CANCELLED = 0;
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_ACTIVE = 2;
    public static final int STATUS_COMPLETED = 3;

    protected String id;
    protected String bookId;
    protected int copyNumber;
    protected String userEmail;
    protected int status;
    protected Date initialDate;
    protected Date finalDate;
    protected Date checkInDate;
    protected Date checkOutDate;

    public Borrow() {
    }

    public Borrow(com.nearsoft.nearbooks.models.realm.Borrow borrow) {
        id = borrow.getId();
        bookId = borrow.getBookId();
        copyNumber = borrow.getCopyNumber();
        userEmail = borrow.getUserEmail();
        status = borrow.getStatus();
        initialDate = borrow.getInitialDate();
        finalDate = borrow.getFinalDate();
        checkInDate = borrow.getCheckInDate();
        checkOutDate = borrow.getCheckOutDate();
    }

    protected Borrow(Parcel in) {
        this.id = in.readString();
        this.bookId = in.readString();
        this.copyNumber = in.readInt();
        this.userEmail = in.readString();
        this.status = in.readInt();
        long tmpInitialDate = in.readLong();
        this.initialDate = tmpInitialDate == -1 ? null : new Date(tmpInitialDate);
        long tmpFinalDate = in.readLong();
        this.finalDate = tmpFinalDate == -1 ? null : new Date(tmpFinalDate);
        long tmpCheckInDate = in.readLong();
        this.checkInDate = tmpCheckInDate == -1 ? null : new Date(tmpCheckInDate);
        long tmpCheckOutDate = in.readLong();
        this.checkOutDate = tmpCheckOutDate == -1 ? null : new Date(tmpCheckOutDate);
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
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
        notifyPropertyChanged(BR.bookId);
    }

    @Bindable
    public int getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(int copyNumber) {
        this.copyNumber = copyNumber;
        notifyPropertyChanged(BR.copyNumber);
    }

    @Bindable
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        notifyPropertyChanged(BR.userEmail);
    }

    @Bindable
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
        notifyPropertyChanged(BR.initialDate);
    }

    @Bindable
    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
        notifyPropertyChanged(BR.finalDate);
    }

    @Bindable
    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
        notifyPropertyChanged(BR.checkInDate);
    }

    @Bindable
    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
        notifyPropertyChanged(BR.checkOutDate);
    }

    public String getDisplayStatus(Context context) {
        switch (status) {
            case STATUS_REQUESTED:
                return context.getString(R.string.label_book_requested, userEmail);
            case STATUS_ACTIVE:
                return context.getString(R.string.label_book_borrowed, userEmail);
            default:
                return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.bookId);
        dest.writeInt(this.copyNumber);
        dest.writeString(this.userEmail);
        dest.writeInt(this.status);
        dest.writeLong(initialDate != null ? initialDate.getTime() : -1);
        dest.writeLong(finalDate != null ? finalDate.getTime() : -1);
        dest.writeLong(checkInDate != null ? checkInDate.getTime() : -1);
        dest.writeLong(checkOutDate != null ? checkOutDate.getTime() : -1);
    }

    @IntDef({
            STATUS_CANCELLED,
            STATUS_REQUESTED,
            STATUS_ACTIVE,
            STATUS_COMPLETED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

}
