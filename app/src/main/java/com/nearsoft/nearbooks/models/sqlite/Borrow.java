package com.nearsoft.nearbooks.models.sqlite;

import android.content.Context;
import android.databinding.Bindable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.android.databinding.library.baseAdapters.BR;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.nearsoft.nearbooks.models.BorrowModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Borrow sqlite model.
 * Created by epool on 1/7/16.
 */
@TableEndpoint(name = Borrow.NAME, contentProviderName = NearbooksDatabase.CONTENT_PROVIDER_NAME)
@Table(database = NearbooksDatabase.class)
public class Borrow extends NearbooksBaseObservableModel implements Parcelable {

    public static final int STATUS_CANCELLED = 0;
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_ACTIVE = 2;
    public static final int STATUS_COMPLETED = 3;

    public static final String NAME = "Borrow";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE)
    public static final Uri CONTENT_URI = buildUri(NAME);

    public static final Parcelable.Creator<Borrow> CREATOR = new Parcelable.Creator<Borrow>() {
        public Borrow createFromParcel(Parcel source) {
            return new Borrow(source);
        }

        public Borrow[] newArray(int size) {
            return new Borrow[size];
        }
    };

    @Column
    @PrimaryKey
    @SerializedName("borrowID")
    protected String id;

    @ForeignKey(saveForeignKeyModel = false)
    @SerializedName("bookID")
    protected ForeignKeyContainer<Book> bookForeignKeyContainer;

    @Column
    @SerializedName("copyNumber")
    protected int copyNumber;

    @Column
    @SerializedName("userEmail")
    protected String userEmail;

    @Column
    @SerializedName("status")
    protected int status;

    @Column
    @SerializedName("initialDate")
    protected Date initialDate;

    @Column
    @SerializedName("finalDate")
    protected Date finalDate;

    @Column
    @SerializedName("checkIn")
    protected Date checkInDate;

    @Column
    @SerializedName("checkOut")
    protected Date checkOutDate;

    public Borrow() {
    }

    protected Borrow(Parcel in) {
        this.id = in.readString();
        this.bookForeignKeyContainer = BorrowModel
                .bookForeignKeyContainerFromBookId(in.readString());
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
    public ForeignKeyContainer<Book> getBookForeignKeyContainer() {
        return bookForeignKeyContainer;
    }

    public void setBookForeignKeyContainer(ForeignKeyContainer<Book> bookForeignKeyContainer) {
        this.bookForeignKeyContainer = bookForeignKeyContainer;
        notifyPropertyChanged(BR.bookForeignKeyContainer);
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
    @Status
    public int getStatus() {
        return status;
    }

    public void setStatus(@Status int status) {
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

    public void associateBook(Book book) {
        bookForeignKeyContainer = FlowManager
                .getContainerAdapter(Book.class)
                .toForeignKeyContainer(book);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Borrow borrow = (Borrow) o;
        return copyNumber == borrow.copyNumber &&
                status == borrow.status &&
                Objects.equal(id, borrow.id) &&
                Objects.equal(bookForeignKeyContainer, borrow.bookForeignKeyContainer) &&
                Objects.equal(userEmail, borrow.userEmail) &&
                Objects.equal(initialDate, borrow.initialDate) &&
                Objects.equal(finalDate, borrow.finalDate) &&
                Objects.equal(checkInDate, borrow.checkInDate) &&
                Objects.equal(checkOutDate, borrow.checkOutDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                id,
                bookForeignKeyContainer,
                copyNumber,
                userEmail, status,
                initialDate,
                finalDate,
                checkInDate,
                checkOutDate
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(
                BorrowModel.bookIdFromBookForeignKeyContainer(this.bookForeignKeyContainer)
        );
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
