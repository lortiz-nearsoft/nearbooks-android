package com.nearsoft.nearbooks.exceptions

import android.content.Context
import android.support.annotation.StringRes

/**
 * Base exception class.
 * Created by epool on 1/20/16.
 */
open class NearbooksException
/**
 * Constructs a new `NearbooksException` with the current stack trace and the
 * specified detail message.

 * @param detailMessage the detail message for this exception.
 * *
 * @param resourceId    String id of the message to display.
 * *
 * @param formatArgs    arguments for the message to display.
 */
(detailMessage: String, @StringRes private val mResourceId: Int,
 vararg formatArgs: Any) : Exception(detailMessage) {

    private val mFormatArgs: Array<Any>

    init {
        mFormatArgs = arrayOf(formatArgs)
    }

    fun getDisplayMessage(context: Context): String {
        return context.getString(mResourceId, *mFormatArgs)
    }

}
