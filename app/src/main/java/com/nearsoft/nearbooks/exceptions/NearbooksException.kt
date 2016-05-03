package com.nearsoft.nearbooks.exceptions

import android.content.Context
import android.support.annotation.StringRes

/**
 * Base exception class.
 * Created by epool on 1/20/16.
 */
open class NearbooksException(
        detailMessage: String,
        @StringRes private val mResourceId: Int,
        vararg formatArgs: Any
) : Exception(detailMessage) {

    private val mFormatArgs: Array<Any>

    init {
        mFormatArgs = arrayOf(formatArgs)
    }

    fun getDisplayMessage(context: Context): String {
        return context.getString(mResourceId, *mFormatArgs)
    }

}
