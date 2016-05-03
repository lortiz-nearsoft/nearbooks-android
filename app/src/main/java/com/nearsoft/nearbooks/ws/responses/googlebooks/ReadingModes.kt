package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class ReadingModes(
        @SerializedName("text") var isText: Boolean = false,
        @SerializedName("image") var isImage: Boolean = false
)
