package com.nearsoft.nearbooks.sync

import android.content.ContentResolver
import android.content.SyncStatusObserver
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.util.SyncUtil
import dagger.Lazy

/**
 * Class to handle sync changes.
 * Created by epool on 12/28/15.
 */
class SyncChangeHandler(private val mLazyUser: Lazy<User>) {

    private val mOnSyncChangeListeners: MutableList<OnSyncChangeListener> = mutableListOf()

    private var mIsSyncing = false

    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     *
     *
     *
     * This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private var mSyncObserverHandle: Any? = null

    /**
     * Create a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private val mSyncStatusObserver = SyncStatusObserver {
        if (!mOnSyncChangeListeners.isEmpty()) {
            val isSyncing = SyncUtil.isSyncing(mLazyUser.get())
            if (mIsSyncing != isSyncing) {
                mIsSyncing = isSyncing
                for (onSyncChangeListener in mOnSyncChangeListeners) {
                    onSyncChangeListener.onSyncChange(mIsSyncing)
                }
            }
        }
    }

    fun onResume() {
        mSyncStatusObserver.onStatusChanged(0)

        // Watch for sync state changes
        val mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING or ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver)
    }

    fun onPause() {
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle)
            mSyncObserverHandle = null
        }
    }

    fun addOnSyncChangeListener(onSyncChangeListener: OnSyncChangeListener) {
        mOnSyncChangeListeners.add(onSyncChangeListener)
    }

    fun removeOnSyncChangeListener(onSyncChangeListener: OnSyncChangeListener) {
        mOnSyncChangeListeners.remove(onSyncChangeListener)
    }

    fun removeAllOnSyncChangeListener() {
        mOnSyncChangeListeners.clear()
    }

    interface OnSyncChangeListener {
        fun onSyncChange(isSyncing: Boolean)
    }

}
