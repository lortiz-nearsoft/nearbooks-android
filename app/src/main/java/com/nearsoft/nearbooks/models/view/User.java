package com.nearsoft.nearbooks.models.view;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nearsoft.nearbooks.BR;

/**
 * User view model.
 * Created by epool on 5/1/16.
 */
public class User extends BaseObservable implements Parcelable {

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private String id;
    private String displayName;
    private String email;
    private String photoUrl;
    private String idToken;

    public User() {
    }

    public User(GoogleSignInAccount googleSignInAccount) {
        id = googleSignInAccount.getId();
        displayName = googleSignInAccount.getDisplayName();
        email = googleSignInAccount.getEmail();
        idToken = googleSignInAccount.getIdToken();
        Uri photoUri = googleSignInAccount.getPhotoUrl();
        if (photoUri != null) {
            photoUrl = photoUri.toString();
        }
    }

    public User(com.nearsoft.nearbooks.models.realm.User user) {
        id = user.getId();
        displayName = user.getDisplayName();
        email = user.getEmail();
        photoUrl = user.getPhotoUrl();
        idToken = user.getIdToken();
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.displayName = in.readString();
        this.email = in.readString();
        this.photoUrl = in.readString();
        this.idToken = in.readString();
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
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        notifyPropertyChanged(BR.displayName);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        notifyPropertyChanged(BR.photoUrl);
    }

    @Bindable
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
        notifyPropertyChanged(BR.idToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.displayName);
        dest.writeString(this.email);
        dest.writeString(this.photoUrl);
        dest.writeString(this.idToken);
    }
}
