package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class SaleInfo(
        @SerializedName("country") var country: String? = null,
        @SerializedName("saleability") var saleability: String? = null,
        @SerializedName("isEbook") var isIsEbook: Boolean = false
)
