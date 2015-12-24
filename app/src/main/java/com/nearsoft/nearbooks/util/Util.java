package com.nearsoft.nearbooks.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utilities class.
 * Created by epool on 12/23/15.
 */
public class Util {

    public static boolean isThereInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null &&
                networkInfo.isConnected() &&
                networkInfo.isAvailable();
    }

}
