package com.nearsoft.nearbooks.exceptions;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Base exception class.
 * Created by epool on 1/20/16.
 */
public abstract class NearbooksException extends Exception {

    private final int mResourceId;
    private final Object[] mFormatArgs;

    /**
     * Constructs a new {@code NearbooksException} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     * @param resourceId    String id of the message to display.
     * @param formatArgs    arguments for the message to display.
     */
    public NearbooksException(String detailMessage, @StringRes int resourceId,
                              Object... formatArgs) {
        super(detailMessage);
        mResourceId = resourceId;
        mFormatArgs = formatArgs;
    }

    public String getDisplayMessage(Context context) {
        return context.getString(mResourceId, mFormatArgs);
    }

}
