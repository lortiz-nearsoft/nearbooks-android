package com.nearsoft.nearbooks.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.common.Constants;
import com.nearsoft.nearbooks.models.SharedPreferenceModel;

import java.io.IOException;

/**
 * Registration intent service.
 * Created by epool on 1/19/16.
 */
public class NearbooksRegistrationIntentService extends IntentService {

    private static final String TAG = NearbooksRegistrationIntentService.class.getSimpleName();

    public NearbooksRegistrationIntentService() {
        super(TAG);
        NearbooksApplication.Companion.applicationComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            SharedPreferenceModel.putBoolean(
                    SharedPreferenceModel.PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER,
                    registerTokenInServer(token)
            );
        } catch (IOException e) {
            e.printStackTrace();
            SharedPreferenceModel.putBoolean(
                    SharedPreferenceModel.PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER,
                    false
            );
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private boolean registerTokenInServer(String token) {
        // TODO: Send the token to the server.
        return true;
    }

}
