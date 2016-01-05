package com.nearsoft.nearbooks.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.nearsoft.nearbooks.sync.auth.NearbooksAccountAuthenticator;

/**
 * Nearbooks authenticator service.
 * Created by epool on 12/21/15.
 */
public class NearbooksAuthenticatorService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        NearbooksAccountAuthenticator nearbooksAccountAuthenticator =
                new NearbooksAccountAuthenticator(this);
        return nearbooksAccountAuthenticator.getIBinder();
    }

}
