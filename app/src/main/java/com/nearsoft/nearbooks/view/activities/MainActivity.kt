package com.nearsoft.nearbooks.view.activities

import android.accounts.AccountManager
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast

import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.common.Constants
import com.nearsoft.nearbooks.di.components.BaseActivityComponent
import com.nearsoft.nearbooks.di.modules.BaseActivityModule
import com.nearsoft.nearbooks.gcm.NearbooksRegistrationIntentService
import com.nearsoft.nearbooks.sync.auth.AccountGeneral
import com.nearsoft.nearbooks.util.ErrorUtil
import com.nearsoft.nearbooks.util.Util
import com.nearsoft.nearbooks.util.ViewUtil

import java.io.IOException

import javax.inject.Inject
import javax.inject.Named

class MainActivity : BaseActivity() {

    @Inject
    lateinit protected var mAccountManager: AccountManager
    @field:[Inject Named(BaseActivityModule.GCM_BROAD_CAST_RECEIVER)]
    lateinit protected var mRegistrationBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Util.checkPlayServices(this)) {
            ViewUtil.showToastMessage(this, R.string.message_google_play_services_required)
            finish()
            return
        }

        getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE,
                AccountGeneral.AUTH_TOKEN_TYPE_FULL_ACCESS)
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun injectComponent(baseActivityComponent: BaseActivityComponent) {
        super.injectComponent(baseActivityComponent)
        baseActivityComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                IntentFilter(Constants.REGISTRATION_COMPLETE))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver)
        super.onPause()
    }

    private fun getTokenForAccountCreateIfNeeded(accountType: String, authTokenType: String) {
        mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                { future ->
                    try {
                        future.result
                        if (mLazyUser.get() != null) {
                            // Start IntentService to register this application with GCM.
                            val intent = Intent(this@MainActivity,
                                    NearbooksRegistrationIntentService::class.java)
                            startService(intent)
                        } else {
                            Toast.makeText(
                                    this@MainActivity,
                                    ErrorUtil.getGeneralExceptionMessage(this,
                                            getString(
                                                    R.string.error_user_not_found)),
                                    Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } catch (e: OperationCanceledException) {
                        finish()
                    } catch (e: IOException) {
                        Toast.makeText(
                                this@MainActivity,
                                ErrorUtil.getGeneralExceptionMessage(this,
                                        e.message!!),
                                Toast.LENGTH_LONG).show()
                        finish()
                    } catch (e: AuthenticatorException) {
                        Toast.makeText(this@MainActivity, ErrorUtil.getGeneralExceptionMessage(this, e.message!!), Toast.LENGTH_LONG).show()
                        finish()
                    }
                }, null)
    }

}
