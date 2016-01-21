package com.nearsoft.nearbooks.models.sqlite;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.net.Uri;
import android.support.annotation.IntDef;

import com.google.gson.Gson;
import com.nearsoft.nearbooks.NearbooksApplication;
import com.nearsoft.nearbooks.db.NearbooksDatabase;
import com.raizlabs.android.dbflow.structure.provider.BaseSyncableProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Base model.
 * Created by epool on 12/17/15.
 */
public abstract class NearbooksBaseObservableModel<T extends BaseSyncableProviderModel>
        extends BaseSyncableProviderModel<T>
        implements Observable {

    public static final int SQLITE_BOOLEAN_FALSE = 0;
    public static final int SQLITE_BOOLEAN_TRUE = 1;

    private transient PropertyChangeRegistry mCallbacks;

    protected static Uri buildUri(String... paths) {
        Uri.Builder builder = Uri
                .parse(ContentUtils.BASE_CONTENT_URI + NearbooksDatabase.CONTENT_AUTHORITY)
                .buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @Override
    public synchronized void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (mCallbacks == null) {
            mCallbacks = new PropertyChangeRegistry();
        }
        mCallbacks.add(callback);
    }

    @Override
    public synchronized void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    public synchronized void notifyChange() {
        if (mCallbacks != null) {
            mCallbacks.notifyCallbacks(this, 0, null);
        }
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with {@link Bindable} to generate a field in
     * <code>BR</code> to be used as <code>fieldId</code>.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    public void notifyPropertyChanged(int fieldId) {
        if (mCallbacks != null) {
            mCallbacks.notifyCallbacks(this, fieldId, null);
        }
    }

    @Override
    public String toString() {
        Gson gson = NearbooksApplication
                .getNearbooksApplicationComponent()
                .provideGson();
        return gson.toJson(this);
    }

    @IntDef({
            SQLITE_BOOLEAN_FALSE,
            SQLITE_BOOLEAN_TRUE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SqliteBoolean {
    }

}
