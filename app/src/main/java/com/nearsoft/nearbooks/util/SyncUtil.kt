package com.nearsoft.nearbooks.util

import android.accounts.Account
import android.content.ContentResolver
import android.os.Bundle

import com.nearsoft.nearbooks.common.Constants
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.sync.auth.AccountGeneral

/**
 * Sync util.
 * Created by epool on 11/27/15.
 */
object SyncUtil {

    fun configSyncPeriod(account: Account) {
        // Inform the system that this account supports sync
        ContentResolver.setIsSyncable(account, Constants.CONTENT_AUTHORITY, 1)
        // Inform the system that this account is eligible for auto sync when the network is up
        ContentResolver.setSyncAutomatically(account, Constants.CONTENT_AUTHORITY, true)
        // Recommend a schedule for automatic synchronization. The system may modify this based
        // on other scheduled syncs and network utilization.
        ContentResolver.addPeriodicSync(
                account,
                Constants.CONTENT_AUTHORITY,
                Bundle.EMPTY,
                AccountGeneral.SYNC_FREQUENCY
        )
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     *
     *
     *
     * This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     *
     *
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    fun triggerRefresh(user: User) {
        val b = Bundle()
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        val account = Account(user.email, AccountGeneral.ACCOUNT_TYPE)
        ContentResolver.requestSync(account, Constants.CONTENT_AUTHORITY, b)
    }

    fun isSyncing(user: User): Boolean {
        val account = Account(user.email, AccountGeneral.ACCOUNT_TYPE)
        return ContentResolver.isSyncActive(account, Constants.CONTENT_AUTHORITY)
    }

}
