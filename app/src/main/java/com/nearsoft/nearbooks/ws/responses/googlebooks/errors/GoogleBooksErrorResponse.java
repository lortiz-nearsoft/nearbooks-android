package com.nearsoft.nearbooks.ws.responses.googlebooks.errors;

import com.google.gson.annotations.SerializedName;

/**
 * Google books error response.
 * Created by epool on 2/9/16.
 */
public class GoogleBooksErrorResponse {

    @SerializedName("error")
    private GoogleBooksError error;

    public GoogleBooksError getError() {
        return error;
    }

    public void setError(GoogleBooksError error) {
        this.error = error;
    }

}
