package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class Epub(
        @SerializedName("isAvailable") var isIsAvailable: Boolean = false
)
