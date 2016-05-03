package com.nearsoft.nearbooks.sync.auth

import com.nearsoft.nearbooks.BuildConfig

/**
 * Account general.
 * Created by epool on 12/21/15.
 */
class AccountGeneral {

    companion object {
        // Value below must match the account type specified in res/xml/sync_adapter.xml
        const val ACCOUNT_TYPE = "${BuildConfig.APPLICATION_ID}.account"

        const val SYNC_FREQUENCY = 60L * 60L // 1 hour (in seconds)
        const val PREF_SETUP_COMPLETE = "setup_complete"

        const val AUTH_TOKEN_TYPE_READ_ONLY = "Read only"
        const val AUTH_TOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Nearsoft account"

        const val AUTH_TOKEN_TYPE_FULL_ACCESS = "Full access"
        const val AUTH_TOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Nearsoft account"
    }

}
