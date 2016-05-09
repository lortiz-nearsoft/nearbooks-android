package com.nearsoft.nearbooks.gcm

import android.os.Bundle
import com.google.android.gms.gcm.GcmListenerService
import com.nearsoft.nearbooks.extensions.logD

/**
 * GCM listener.
 * Created by epool on 1/19/16.
 */
class NearbooksGcmListenerService : GcmListenerService() {

    /**
     * Called when message is received.

     * @param from SenderID of the sender.
     * *
     * @param data Data bundle containing message data as key/value pairs.
     * *             For Set of keys use data.keySet().
     */
    override fun onMessageReceived(from: String?, data: Bundle?) {
        val message = data!!.getString("message")
        logD("From: " + from!!)
        logD("Message: " + message)
    }

}
