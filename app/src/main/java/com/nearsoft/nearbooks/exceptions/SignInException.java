package com.nearsoft.nearbooks.exceptions;

import android.support.annotation.StringRes;

/**
 * Exception for sign in errors.
 * Created by epool on 1/4/16.
 */
public class SignInException extends NearbooksException {

    /**
     * Constructs a new {@code NearbooksException} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     * @param resourceId    String id of the message to display.
     * @param formatArgs    arguments for the message to display.
     */
    public SignInException(String detailMessage, @StringRes int resourceId, Object... formatArgs) {
        super(detailMessage, resourceId, formatArgs);
    }

}
