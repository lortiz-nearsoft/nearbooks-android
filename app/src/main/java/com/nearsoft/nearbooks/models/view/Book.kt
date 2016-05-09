package com.nearsoft.nearbooks.models.view

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import com.nearsoft.nearbooks.BR

/**
 * Book view model.
 * Created by epool on 5/1/16.
 */
class Book : BaseObservable, Parcelable {

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Book> = object : Parcelable.Creator<Book> {
            override fun createFromParcel(source: Parcel): Book {
                return Book(source)
            }

            override fun newArray(size: Int): Array<Book?> {
                return arrayOfNulls(size)
            }
        }

    }

    var id: String? = null
        @Bindable get() = field
        set(id) {
            field = id
            notifyPropertyChanged(BR.id)
        }
    var title: String? = null
        @Bindable get() = field
        set(title) {
            field = title
            notifyPropertyChanged(BR.title)
        }
    var author: String? = null
        @Bindable get() = field
        set(author) {
            field = author
            notifyPropertyChanged(BR.author)
        }
    var releaseYear: String? = null
        @Bindable get() = field
        set(releaseYear) {
            field = releaseYear
            notifyPropertyChanged(BR.releaseYear)
        }
    var description: String? = null
        @Bindable get() = field
        set(description) {
            field = description
            notifyPropertyChanged(BR.description)
        }
    var numberOfCopies: Int = 0
        @Bindable get() = field
        set(numberOfCopies) {
            field = numberOfCopies
            notifyPropertyChanged(BR.numberOfCopies)
        }
    var numberOfDaysAllowedForBorrowing: Int = 0
        @Bindable get() = field
        set(numberOfDaysAllowedForBorrowing) {
            field = numberOfDaysAllowedForBorrowing
            notifyPropertyChanged(BR.numberOfDaysAllowedForBorrowing)
        }
    var isAvailable: Boolean = false
        @Bindable get() = field
        set(available) {
            field = available
            notifyPropertyChanged(BR.available)
        }
    var borrows: MutableList<Borrow> = mutableListOf()
        @Bindable get() = field
        set(borrows) {
            field = borrows
            notifyPropertyChanged(BR.borrows)
        }

    constructor() {
    }

    constructor(book: com.nearsoft.nearbooks.models.realm.Book) {
        id = book.id
        title = book.title
        author = book.author
        releaseYear = book.releaseYear
        description = book.description
        numberOfCopies = book.numberOfCopies
        numberOfDaysAllowedForBorrowing = book.numberOfDaysAllowedForBorrowing
        isAvailable = book.isAvailable
        for (borrow in book.borrows.orEmpty()) {
            borrows.add(Borrow(borrow))
        }
    }

    protected constructor(parcel: Parcel) {
        id = parcel.readString()
        title = parcel.readString()
        author = parcel.readString()
        releaseYear = parcel.readString()
        description = parcel.readString()
        numberOfCopies = parcel.readInt()
        numberOfDaysAllowedForBorrowing = parcel.readInt()
        isAvailable = parcel.readByte().toInt() != 0
        parcel.readList(borrows, Borrow::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(title)
        dest.writeString(author)
        dest.writeString(releaseYear)
        dest.writeString(description)
        dest.writeInt(numberOfCopies)
        dest.writeInt(numberOfDaysAllowedForBorrowing)
        dest.writeByte(if (isAvailable) 1.toByte() else 0.toByte())
        dest.writeList(borrows)
    }

}
