package com.nearsoft.nearbooks.util;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;

/**
 * Sync util.
 * Created by epool on 11/27/15.
 */
public class SyncUtil {

    public static void configSyncPeriod(Account account) {
        // Inform the system that this account supports sync
        ContentResolver.setIsSyncable(account, NearbooksDatabase.CONTENT_AUTHORITY, 1);
        // Inform the system that this account is eligible for auto sync when the network is up
        ContentResolver.setSyncAutomatically(account, NearbooksDatabase.CONTENT_AUTHORITY, true);
        // Recommend a schedule for automatic synchronization. The system may modify this based
        // on other scheduled syncs and network utilization.
        ContentResolver.addPeriodicSync(
                account,
                NearbooksDatabase.CONTENT_AUTHORITY,
                Bundle.EMPTY,
                AccountGeneral.SYNC_FREQUENCY
        );
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     * <p/>
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     * <p/>
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    public static void triggerRefresh(User user) {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        Account account = new Account(user.getEmail(), AccountGeneral.ACCOUNT_TYPE);
        ContentResolver.requestSync(account, NearbooksDatabase.CONTENT_AUTHORITY, b);
    }

    public static boolean isSyncing(User user) {
        Account account = new Account(user.getEmail(), AccountGeneral.ACCOUNT_TYPE);
        return ContentResolver
                .isSyncActive(account, NearbooksDatabase.CONTENT_AUTHORITY);
    }

}
