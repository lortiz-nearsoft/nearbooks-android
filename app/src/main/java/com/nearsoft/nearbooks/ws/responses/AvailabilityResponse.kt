package com.nearsoft.nearbooks.ws.responses

import com.google.gson.annotations.SerializedName
import com.nearsoft.nearbooks.models.realm.Book
import com.nearsoft.nearbooks.models.realm.Borrow
import java.util.*

/**
 * Availability response.
 * Created by epool on 1/12/16.
 */
data class AvailabilityResponse(
        @SerializedName("codeQRID") val codeQrId: String? = null,
        @SerializedName("bookID") val bookId: String? = null,
        @SerializedName("copyNumber") val copyNumber: Int,
        @SerializedName("isAvailable") val isAvailable: Boolean,
        @SerializedName("nextStatus") val nextStatus: Int,
        @SerializedName("initialDateAvailability") val initialDateAvailability: Date? = null,
        @SerializedName("finalDateAvailability") val finalDateAvailability: Date? = null,
        @SerializedName("message") val message: String? = null,
        @SerializedName("activeBorrrow") val activeBorrow: Borrow? = null,
        @SerializedName("book") val book: Book? = null
)
