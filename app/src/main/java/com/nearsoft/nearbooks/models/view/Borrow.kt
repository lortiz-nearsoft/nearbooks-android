package com.nearsoft.nearbooks.models.view

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable;
import android.os.Parcel
import android.os.Parcelable
import com.nearsoft.nearbooks.BR
import com.nearsoft.nearbooks.R
import java.util.*

/**
 * Borrow view model.
 * Created by epool on 5/1/16.
 */
class Borrow : BaseObservable, Parcelable {

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Borrow> = object : Parcelable.Creator<Borrow> {
            override fun createFromParcel(source: Parcel): Borrow {
                return Borrow(source)
            }

            override fun newArray(size: Int): Array<Borrow?> {
                return arrayOfNulls(size)
            }
        }

        const val STATUS_CANCELLED = 0
        const val STATUS_REQUESTED = 1
        const val STATUS_ACTIVE = 2
        const val STATUS_COMPLETED = 3

    }

    var id: String? = null
        @Bindable get() = field
        set(id) {
            field = id
            notifyPropertyChanged(BR.id)
        }
    var bookId: String? = null
        @Bindable get() = field
        set(bookId) {
            field = bookId
            notifyPropertyChanged(BR.bookId)
        }
    var copyNumber: Int = 0
        @Bindable get() = field
        set(copyNumber) {
            field = copyNumber
            notifyPropertyChanged(BR.copyNumber)
        }
    var userEmail: String? = null
        @Bindable get() = field
        set(userEmail) {
            field = userEmail
            notifyPropertyChanged(BR.userEmail)
        }
    var status: Int = 0
        @Bindable get() = field
        set(status) {
            field = status
            notifyPropertyChanged(BR.status)
        }
    var initialDate: Date? = null
        @Bindable get() = field
        set(initialDate) {
            field = initialDate
            notifyPropertyChanged(BR.initialDate)
        }
    var finalDate: Date? = null
        @Bindable get() = field
        set(finalDate) {
            field = finalDate
            notifyPropertyChanged(BR.finalDate)
        }
    var checkInDate: Date? = null
        @Bindable get() = field
        set(checkInDate) {
            field = checkInDate
            notifyPropertyChanged(BR.checkInDate)
        }
    var checkOutDate: Date? = null
        @Bindable get() = field
        set(checkOutDate) {
            field = checkOutDate
            notifyPropertyChanged(BR.checkOutDate)
        }

    constructor() {
    }

    constructor(borrow: com.nearsoft.nearbooks.models.realm.Borrow) {
        id = borrow.id
        bookId = borrow.bookId
        copyNumber = borrow.copyNumber
        userEmail = borrow.userEmail
        status = borrow.status
        initialDate = borrow.initialDate
        finalDate = borrow.finalDate
        checkInDate = borrow.checkInDate
        checkOutDate = borrow.checkOutDate
    }

    protected constructor(parcel: Parcel) {
        id = parcel.readString()
        bookId = parcel.readString()
        copyNumber = parcel.readInt()
        userEmail = parcel.readString()
        status = parcel.readInt()
        val tmpInitialDate = parcel.readLong()
        initialDate = if (tmpInitialDate == -1L) null else Date(tmpInitialDate)
        val tmpFinalDate = parcel.readLong()
        finalDate = if (tmpFinalDate == -1L) null else Date(tmpFinalDate)
        val tmpCheckInDate = parcel.readLong()
        checkInDate = if (tmpCheckInDate == -1L) null else Date(tmpCheckInDate)
        val tmpCheckOutDate = parcel.readLong()
        checkOutDate = if (tmpCheckOutDate == -1L) null else Date(tmpCheckOutDate)
    }

    fun getDisplayStatus(context: Context): String? {
        when (status) {
            STATUS_REQUESTED -> return context.getString(R.string.label_book_requested, userEmail)
            STATUS_ACTIVE -> return context.getString(R.string.label_book_borrowed, userEmail)
            else -> return null
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(bookId)
        dest.writeInt(copyNumber)
        dest.writeString(userEmail)
        dest.writeInt(status)
        dest.writeLong(if (initialDate != null) initialDate!!.time else -1L)
        dest.writeLong(if (finalDate != null) finalDate!!.time else -1L)
        dest.writeLong(if (checkInDate != null) checkInDate!!.time else -1L)
        dest.writeLong(if (checkOutDate != null) checkOutDate!!.time else -1L)
    }

}