package com.nearsoft.nearbooks.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.nearsoft.nearbooks.sync.auth.NearbooksAccountAuthenticator

/**
 * Nearbooks authenticator service.
 * Created by epool on 12/21/15.
 */
class NearbooksAuthenticatorService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        val nearbooksAccountAuthenticator = NearbooksAccountAuthenticator(this)
        return nearbooksAccountAuthenticator.iBinder
    }

}
