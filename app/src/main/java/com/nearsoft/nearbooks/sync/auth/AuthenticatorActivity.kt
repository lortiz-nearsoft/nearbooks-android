package com.nearsoft.nearbooks.sync.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.databinding.ActivityAuthenticatorBinding
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent
import com.nearsoft.nearbooks.exceptions.NearbooksException
import com.nearsoft.nearbooks.models.UserModel
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.util.SyncUtil
import com.nearsoft.nearbooks.util.ViewUtil

import javax.inject.Inject

/**
 * Nearbooks authenticator activity.
 * Created by epool on 12/21/15.
 */
class AuthenticatorActivity : AccountAuthenticatorAppCompatActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var mBinding: ActivityAuthenticatorBinding? = null
    private var mAccountManager: AccountManager? = null
    private var mAuthTokenType: String? = null
    private var mAccountType: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getBinding(ActivityAuthenticatorBinding::class.java)

        mAccountManager = AccountManager.get(baseContext)

        mAuthTokenType = intent.getStringExtra(ARG_AUTH_TOKEN_TYPE)
        mAccountType = intent.getStringExtra(ARG_ACCOUNT_TYPE)
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTH_TOKEN_TYPE_FULL_ACCESS
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_authenticator
    }

    override fun injectComponent(googleApiClientComponent: GoogleApiClientComponent) {
        googleApiClientComponent.inject(this)
    }

    fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            finishLogin(result)
        }
    }

    private fun finishLogin(result: GoogleSignInResult) {
        try {
            createSyncAccount(UserModel.signIn(this, result))
        } catch (e: NearbooksException) {
            ViewUtil.showSnackbarMessage(mBinding!!, e.getDisplayMessage(this))
            if (mGoogleApiClient.isConnected) {
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            }
        }

    }

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     */
    private fun createSyncAccount(user: User) {
        var newAccount = false
        val setupComplete = sharedPreferences.getBoolean(AccountGeneral.PREF_SETUP_COMPLETE, false)

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        val account = Account(user.email, mAccountType)
        if (mAccountManager!!.addAccountExplicitly(account, null, null)) {
            mAccountManager!!.setAuthToken(account, mAuthTokenType, user.idToken)

            SyncUtil.configSyncPeriod(account)
            newAccount = true

            val bundle = Bundle()
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, user.email)
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType)

            val intent = Intent().putExtras(bundle)

            setAccountAuthenticatorResult(intent.extras)
            setResult(RESULT_OK, intent)
            finish()
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            SyncUtil.triggerRefresh(user)

            sharedPreferences.edit().putBoolean(AccountGeneral.PREF_SETUP_COMPLETE, true).apply()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        mBinding!!.signInButton.setOnClickListener { v -> signIn() }
    }

    companion object {

        val ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE"
        val ARG_AUTH_TOKEN_TYPE = "AUTH_TYPE"
        val ARG_ACCOUNT_NAME = "ACCOUNT_NAME"

        private val RC_SIGN_IN = 1
    }
}
