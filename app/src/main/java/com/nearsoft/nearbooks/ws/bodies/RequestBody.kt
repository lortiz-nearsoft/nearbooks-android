package com.nearsoft.nearbooks.ws.bodies

import com.google.gson.annotations.SerializedName

/**
 * Request body.
 * Created by epool on 1/12/16.
 */
data class RequestBody(
        @SerializedName("codeQR") var qrCode: String? = null,
        @SerializedName("userEmail") var userEmail: String? = null
)
