package com.nearsoft.nearbooks.ws.responses

import com.google.gson.annotations.SerializedName

/**
 * Message response.
 * Created by epool on 1/13/16.
 */
data class MessageResponse(
        @SerializedName("id") val messageId: Int,
        @SerializedName("message") val message: String? = null
)
