package com.nearsoft.nearbooks.extensions

import android.util.Log

/**
 * Extensions for Any objects.
 * Created by epool on 5/2/16.
 */

fun Any.logD(message: String) {
    Log.d(this.javaClass.simpleName, "===========> $message")
}

fun Any.logI(message: String) {
    Log.d(this.javaClass.simpleName, "===========> $message")
}