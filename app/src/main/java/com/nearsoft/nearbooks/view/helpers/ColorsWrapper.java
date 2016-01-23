package com.nearsoft.nearbooks.view.helpers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.graphics.Palette;

/**
 * Wrapper to put the colors extracted from a image with the Palette api.
 * Created by epool on 1/13/16.
 */
public class ColorsWrapper implements Parcelable {

    public static final Parcelable.Creator<ColorsWrapper> CREATOR = new Parcelable.Creator<ColorsWrapper>() {
        public ColorsWrapper createFromParcel(Parcel source) {
            return new ColorsWrapper(source);
        }

        public ColorsWrapper[] newArray(int size) {
            return new ColorsWrapper[size];
        }
    };

    private final int mStatusBarColor;
    private final int mBackgroundColor;
    private final int mTitleTextColor;
    private final int mBodyTextColor;

    public ColorsWrapper(int backgroundColor, Palette.Swatch swatch) {
        mBackgroundColor = backgroundColor;
        mStatusBarColor = swatch.getTitleTextColor();
        mTitleTextColor = swatch.getTitleTextColor();
        mBodyTextColor = swatch.getBodyTextColor();
    }

    public ColorsWrapper(int statusBarColor, int backgroundColor, int titleTextColor,
                         int bodyTextColor) {
        mStatusBarColor = statusBarColor;
        mBackgroundColor = backgroundColor;
        mTitleTextColor = titleTextColor;
        mBodyTextColor = bodyTextColor;
    }

    protected ColorsWrapper(Parcel in) {
        this.mStatusBarColor = in.readInt();
        this.mBackgroundColor = in.readInt();
        this.mTitleTextColor = in.readInt();
        this.mBodyTextColor = in.readInt();
    }

    public int getStatusBarColor() {
        return mStatusBarColor;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public int getTitleTextColor() {
        return mTitleTextColor;
    }

    public int getBodyTextColor() {
        return mBodyTextColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatusBarColor);
        dest.writeInt(this.mBackgroundColor);
        dest.writeInt(this.mTitleTextColor);
        dest.writeInt(this.mBodyTextColor);
    }

}
