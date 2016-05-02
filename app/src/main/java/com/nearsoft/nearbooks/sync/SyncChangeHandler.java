package com.nearsoft.nearbooks.sync;

import android.content.ContentResolver;
import android.content.SyncStatusObserver;

import com.nearsoft.nearbooks.models.view.User;
import com.nearsoft.nearbooks.util.SyncUtil;

import java.util.ArrayList;
import java.util.List;

import dagger.Lazy;

/**
 * Class to handle sync changes.
 * Created by epool on 12/28/15.
 */
public class SyncChangeHandler {

    private Lazy<User> mLazyUser;

    private List<OnSyncChangeListener> mOnSyncChangeListeners;

    private boolean mIsSyncing = false;

    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     * <p>
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private Object mSyncObserverHandle;
    /**
     * Create a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            if (mOnSyncChangeListeners != null && !mOnSyncChangeListeners.isEmpty()) {
                boolean isSyncing = SyncUtil.isSyncing(mLazyUser.get());
                if (mIsSyncing != isSyncing) {
                    mIsSyncing = isSyncing;
                    for (OnSyncChangeListener onSyncChangeListener :
                            mOnSyncChangeListeners) {
                        onSyncChangeListener.onSyncChange(mIsSyncing);
                    }
                }
            }
        }
    };

    public SyncChangeHandler(Lazy<User> lazyUser) {
        mLazyUser = lazyUser;
        mOnSyncChangeListeners = new ArrayList<>();
    }

    public void onResume() {
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    public void onPause() {
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    public void addOnSyncChangeListener(OnSyncChangeListener onSyncChangeListener) {
        mOnSyncChangeListeners.add(onSyncChangeListener);
    }

    public void removeOnSyncChangeListener(OnSyncChangeListener onSyncChangeListener) {
        mOnSyncChangeListeners.remove(onSyncChangeListener);
    }

    public void removeAllOnSyncChangeListener() {
        mOnSyncChangeListeners.clear();
    }

    public interface OnSyncChangeListener {

        void onSyncChange(boolean isSyncing);

    }
}
