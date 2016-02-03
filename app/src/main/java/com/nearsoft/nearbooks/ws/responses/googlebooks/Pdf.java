package com.nearsoft.nearbooks.ws.responses.googlebooks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Pdf {

    @SerializedName("isAvailable")
    @Expose
    private boolean isAvailable;

    /**
     * @return The isAvailable
     */
    public boolean isIsAvailable() {
        return isAvailable;
    }

    /**
     * @param isAvailable The isAvailable
     */
    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

}
