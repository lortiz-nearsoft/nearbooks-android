package com.nearsoft.nearbooks.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.nearsoft.nearbooks.BuildConfig
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.view.activities.BaseActivity

/**
 * Utilities class.
 * Created by epool on 12/23/15.
 */
object Util {

    fun getApplicationVersion(context: Context): String? {
        return context.getString(
                R.string.app_name_with_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
        )
    }

    fun isThereInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected && networkInfo.isAvailable
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    fun checkPlayServices(baseActivity: BaseActivity): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(baseActivity)
        return resultCode == ConnectionResult.SUCCESS
    }

}
