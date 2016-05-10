package com.nearsoft.nearbooks.gcm

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.common.Constants
import com.nearsoft.nearbooks.extensions.logI
import com.nearsoft.nearbooks.models.SharedPreferenceModel
import java.io.IOException

/**
 * Registration intent service.
 * Created by epool on 1/19/16.
 */
class NearbooksRegistrationIntentService : IntentService("NearbooksRegistrationIntentService") {

    init {
        NearbooksApplication.applicationComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent) {
        val instanceID = InstanceID.getInstance(this)
        try {
            val token = instanceID.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null
            )
            logI("GCM Registration Token: " + token)

            SharedPreferenceModel.putBoolean(
                    SharedPreferenceModel.PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER,
                    registerTokenInServer(token)
            )
        } catch (e: IOException) {
            e.printStackTrace()
            SharedPreferenceModel.putBoolean(
                    SharedPreferenceModel.PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER,
                    false
            )
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        val registrationComplete = Intent(Constants.REGISTRATION_COMPLETE)
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
    }

    private fun registerTokenInServer(token: String): Boolean {
        // TODO: Send the token to the server.
        return true
    }

}
