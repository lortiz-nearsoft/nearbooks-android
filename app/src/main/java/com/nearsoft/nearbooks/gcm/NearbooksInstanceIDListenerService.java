package com.nearsoft.nearbooks.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Instance id listener.
 * Created by epool on 1/19/16.
 */
public class NearbooksInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, NearbooksRegistrationIntentService.class);
        startService(intent);
    }

}
