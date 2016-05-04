package com.nearsoft.nearbooks.view.helpers

import android.os.Parcel
import android.os.Parcelable
import android.support.v7.graphics.Palette

/**
 * Wrapper to put the colors extracted from a image with the Palette api.
 * Created by epool on 1/13/16.
 */
class ColorsWrapper : Parcelable {

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<ColorsWrapper> = object : Parcelable.Creator<ColorsWrapper> {
            override fun createFromParcel(source: Parcel): ColorsWrapper {
                return ColorsWrapper(source)
            }

            override fun newArray(size: Int): Array<ColorsWrapper?> {
                return arrayOfNulls(size)
            }
        }
    }

    val statusBarColor: Int
    val backgroundColor: Int
    val titleTextColor: Int
    val bodyTextColor: Int

    constructor(backgroundColor: Int, swatch: Palette.Swatch) {
        this.backgroundColor = backgroundColor
        statusBarColor = swatch.titleTextColor
        titleTextColor = swatch.titleTextColor
        bodyTextColor = swatch.bodyTextColor
    }

    constructor(statusBarColor: Int, backgroundColor: Int, titleTextColor: Int,
                bodyTextColor: Int) {
        this.statusBarColor = statusBarColor
        this.backgroundColor = backgroundColor
        this.titleTextColor = titleTextColor
        this.bodyTextColor = bodyTextColor
    }

    protected constructor(parcel: Parcel) {
        this.statusBarColor = parcel.readInt()
        this.backgroundColor = parcel.readInt()
        this.titleTextColor = parcel.readInt()
        this.bodyTextColor = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.statusBarColor)
        dest.writeInt(this.backgroundColor)
        dest.writeInt(this.titleTextColor)
        dest.writeInt(this.bodyTextColor)
    }

}
