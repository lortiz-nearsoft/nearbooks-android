package com.nearsoft.nearbooks.models

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build

import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.nearsoft.nearbooks.BuildConfig
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.exceptions.NearbooksException
import com.nearsoft.nearbooks.exceptions.SignInException
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.sync.auth.AccountGeneral
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.util.Util
import com.nearsoft.nearbooks.view.activities.BaseActivity

import io.realm.Realm

/**
 * User model.
 * Created by epool on 11/18/15.
 */
object UserModel {

    private val NEARSOFT_EMAIL_DOMAIN = "@nearsoft.com"
    private val UNKNOWN_STATUS_CODE = 12501

    @Throws(NearbooksException::class)
    fun signIn(context: Context, result: GoogleSignInResult): User {
        if (result.isSuccess) {
            val googleSignInAccount = result.signInAccount

            validateNearsoftAccount(googleSignInAccount)

            val user = User(googleSignInAccount!!)

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction { r ->
                r.delete(com.nearsoft.nearbooks.models.realm.User::class.java)
                r.copyToRealm(com.nearsoft.nearbooks.models.realm.User(user))
            }
            realm.close()

            if (!BuildConfig.DEBUG) {
                Crashlytics.setUserIdentifier(user.id)
                Crashlytics.setUserEmail(user.email)
                Crashlytics.setUserName(user.displayName)
            }

            return user
        } else if (!Util.isThereInternetConnection(context)) {
            throw NearbooksException("Internet connection error.",
                    R.string.error_internet_connection)
        } else if (result.status.statusCode != UNKNOWN_STATUS_CODE) {
            val statusCode = result.status.statusCode
            throw NearbooksException(
                    "Google api error: $statusCode.",
                    R.string.error_google_api, statusCode)
        } else {
            throw ErrorUtil.getGeneralException("Unknown Exception.")
        }
    }

    @SuppressWarnings("deprecation")
    fun signOut(baseActivity: BaseActivity,
                user: User,
                googleApiClient: GoogleApiClient,
                onSignOutSuccess: Runnable) {

        val accountManager = AccountManager.get(baseActivity)
        val account = Account(user.email, AccountGeneral.ACCOUNT_TYPE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(
                    account,
                    baseActivity,
                    { future -> if (future.isDone) signOut(googleApiClient, onSignOutSuccess) },
                    null)
        } else {
            accountManager.removeAccount(
                    account,
                    { future -> if (future.isDone) signOut(googleApiClient, onSignOutSuccess) },
                    null)
        }
    }

    @Throws(SignInException::class)
    private fun validateNearsoftAccount(googleSignInAccount: GoogleSignInAccount?) {

        val isNearsoftAccountValid = googleSignInAccount != null &&
                googleSignInAccount.email != null &&
                googleSignInAccount.email!!.endsWith(NEARSOFT_EMAIL_DOMAIN)

        if (!isNearsoftAccountValid) {
            throw SignInException("Nearsoft account needed.",
                    R.string.message_nearsoft_account_needed)
        }
    }

    private fun signOut(googleApiClient: GoogleApiClient, onSignOutSuccess: Runnable?) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient)
        Auth.GoogleSignInApi.signOut(googleApiClient)

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { r -> r.delete(com.nearsoft.nearbooks.models.realm.User::class.java) }
        realm.close()

        onSignOutSuccess?.run()
        SharedPreferenceModel.clear()
    }

}
