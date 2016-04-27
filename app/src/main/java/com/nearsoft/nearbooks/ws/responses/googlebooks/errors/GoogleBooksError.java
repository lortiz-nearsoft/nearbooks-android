package com.nearsoft.nearbooks.ws.responses.googlebooks.errors;

import com.google.gson.annotations.SerializedName;
import com.nearsoft.nearbooks.NearbooksApplication;

/**
 * Google books error.
 * Created by epool on 2/9/16.
 */
public class GoogleBooksError {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return NearbooksApplication.Companion.applicationComponent().provideGson().toJson(this);
    }

}
