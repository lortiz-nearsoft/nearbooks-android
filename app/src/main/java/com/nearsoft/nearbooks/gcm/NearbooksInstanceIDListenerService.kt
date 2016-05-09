package com.nearsoft.nearbooks.gcm

import android.content.Intent

import com.google.android.gms.iid.InstanceIDListenerService

/**
 * Instance id listener.
 * Created by epool on 1/19/16.
 */
class NearbooksInstanceIDListenerService : InstanceIDListenerService() {

    override fun onTokenRefresh() {
        val intent = Intent(this, NearbooksRegistrationIntentService::class.java)
        startService(intent)
    }

}
