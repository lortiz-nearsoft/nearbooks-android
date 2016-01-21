package com.nearsoft.nearbooks.ws.bodies;

import com.google.gson.annotations.SerializedName;

/**
 * Request body.
 * Created by epool on 1/12/16.
 */
public class RequestBody {

    @SerializedName("codeQR")
    private String qrCode;

    @SerializedName("userEmail")
    private String userEmail;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
