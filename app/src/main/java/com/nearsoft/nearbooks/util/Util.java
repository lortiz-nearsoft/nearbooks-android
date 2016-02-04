package com.nearsoft.nearbooks.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

/**
 * Utilities class.
 * Created by epool on 12/23/15.
 */
public class Util {

    public static String getApplicationVersion(Context context) {
        try {
            PackageInfo packageInfo = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return context.getString(
                    R.string.app_name_with_version,
                    packageInfo.versionName,
                    packageInfo.versionCode
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

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
