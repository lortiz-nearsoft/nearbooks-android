package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class IndustryIdentifier(
        @SerializedName("type") var type: String? = null,
        @SerializedName("identifier") var identifier: String? = null
)
