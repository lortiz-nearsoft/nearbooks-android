package com.nearsoft.nearbooks.exceptions;

/**
 * Exception for sign in errors.
 * Created by epool on 1/4/16.
 */
public class SignInException extends Exception {

    /**
     * Constructs a new {@code SignInException} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public SignInException(String detailMessage) {
        super(detailMessage);
    }

}
