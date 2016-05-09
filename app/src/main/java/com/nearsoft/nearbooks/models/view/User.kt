package com.nearsoft.nearbooks.models.view

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.nearsoft.nearbooks.BR

/**
 * User view model.
 * Created by epool on 5/1/16.
 */
class User : BaseObservable, Parcelable {

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User {
                return User(source)
            }

            override fun newArray(size: Int): Array<User?> {
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
    var displayName: String? = null
        @Bindable get() = field
        set(displayName) {
            field = displayName
            notifyPropertyChanged(BR.displayName)
        }
    var email: String? = null
        @Bindable get() = field
        set(email) {
            field = email
            notifyPropertyChanged(BR.email)
        }
    var photoUrl: String? = null
        @Bindable get() = field
        set(photoUrl) {
            field = photoUrl
            notifyPropertyChanged(BR.photoUrl)
        }
    var idToken: String? = null
        @Bindable get() = field
        set(idToken) {
            field = idToken
            notifyPropertyChanged(BR.idToken)
        }

    constructor() {
    }

    constructor(googleSignInAccount: GoogleSignInAccount) {
        id = googleSignInAccount.id
        displayName = googleSignInAccount.displayName
        email = googleSignInAccount.email
        idToken = googleSignInAccount.idToken
        photoUrl = googleSignInAccount.photoUrl?.toString()
    }

    constructor(user: com.nearsoft.nearbooks.models.realm.User) {
        id = user.id
        displayName = user.displayName
        email = user.email
        photoUrl = user.photoUrl
        idToken = user.idToken
    }

    protected constructor(parcel: Parcel) {
        id = parcel.readString()
        displayName = parcel.readString()
        email = parcel.readString()
        photoUrl = parcel.readString()
        idToken = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(displayName)
        dest.writeString(email)
        dest.writeString(photoUrl)
        dest.writeString(idToken)
    }

}
