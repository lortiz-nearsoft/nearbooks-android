package com.nearsoft.nearbooks.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

/**
 * Utilities class.
 * Created by epool on 12/23/15.
 */
public class Util {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean isThereInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null &&
                networkInfo.isConnected() &&
                networkInfo.isAvailable();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(BaseActivity baseActivity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(baseActivity);
        return resultCode == ConnectionResult.SUCCESS;
    }

}
