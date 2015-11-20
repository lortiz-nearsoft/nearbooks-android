package com.nearsoft.nearbooks.view.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nearsoft.nearbooks.BR;

/**
 * User view model.
 * Created by epool on 11/18/15.
 */
public class UserViewModel extends BaseObservable implements Parcelable {
    public static final Parcelable.Creator<UserViewModel> CREATOR = new Parcelable.Creator<UserViewModel>() {
        public UserViewModel createFromParcel(Parcel source) {
            return new UserViewModel(source);
        }

        public UserViewModel[] newArray(int size) {
            return new UserViewModel[size];
        }
    };
    private String id;
    private String displayName;
    private String email;
    private String idToken;

    public UserViewModel(GoogleSignInAccount googleSignInAccount) {
        this.id = googleSignInAccount.getId();
        this.displayName = googleSignInAccount.getDisplayName();
        this.email = googleSignInAccount.getEmail();
        this.idToken = googleSignInAccount.getIdToken();
    }

    protected UserViewModel(Parcel in) {
        this.id = in.readString();
        this.displayName = in.readString();
        this.email = in.readString();
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
        dest.writeString(this.idToken);
    }
}
