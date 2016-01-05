package com.nearsoft.nearbooks.models.sqlite;

import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nearsoft.nearbooks.BR;
import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * User sqlite model
 * Created by epool on 12/18/15.
 */
@Table(database = NearbooksDatabase.class)
public class User extends NearbooksBaseObservableModel implements Parcelable {

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Column
    @PrimaryKey
    private String id;

    @Column
    private String displayName;

    @Column
    private String email;

    @Column
    private String idToken;

    public User() {
    }

    public User(GoogleSignInAccount googleSignInAccount) {
        this.id = googleSignInAccount.getId();
        this.displayName = googleSignInAccount.getDisplayName();
        this.email = googleSignInAccount.getEmail();
        this.idToken = googleSignInAccount.getIdToken();
    }

    protected User(Parcel in) {
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
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (displayName != null ? !displayName.equals(user.displayName) : user.displayName != null)
            return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return !(idToken != null ? !idToken.equals(user.idToken) : user.idToken != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (idToken != null ? idToken.hashCode() : 0);
        return result;
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
