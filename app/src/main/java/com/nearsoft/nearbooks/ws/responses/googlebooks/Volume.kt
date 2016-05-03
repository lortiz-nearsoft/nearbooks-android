package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class Volume(
        @SerializedName("kind") var kind: String? = null,
        @SerializedName("id") var id: String,
        @SerializedName("etag") var etag: String? = null,
        @SerializedName("selfLink") var selfLink: String? = null,
        @SerializedName("volumeInfo") var volumeInfo: VolumeInfo? = null,
        @SerializedName("saleInfo") var saleInfo: SaleInfo? = null,
        @SerializedName("accessInfo") var accessInfo: AccessInfo? = null,
        @SerializedName("searchInfo") var searchInfo: SearchInfo? = null
)
