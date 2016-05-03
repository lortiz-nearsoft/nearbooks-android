package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class AccessInfo(
        @SerializedName("country") var country: String? = null,
        @SerializedName("viewability") var viewAbility: String? = null,
        @SerializedName("embeddable") var isEmbeddable: Boolean = false,
        @SerializedName("publicDomain") var isPublicDomain: Boolean = false,
        @SerializedName("textToSpeechPermission") var textToSpeechPermission: String? = null,
        @SerializedName("epub") var epub: Epub? = null,
        @SerializedName("pdf") var pdf: Pdf? = null,
        @SerializedName("webReaderLink") var webReaderLink: String? = null,
        @SerializedName("accessViewStatus") var accessViewStatus: String? = null,
        @SerializedName("quoteSharingAllowed") var isQuoteSharingAllowed: Boolean = false
)
