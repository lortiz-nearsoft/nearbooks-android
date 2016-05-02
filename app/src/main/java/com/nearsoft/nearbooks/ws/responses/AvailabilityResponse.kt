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
        @SerializedName("codeQRID") val codeQrId: String,
        @SerializedName("bookID") val bookId: String,
        @SerializedName("copyNumber") val copyNumber: Int,
        @SerializedName("isAvailable") val isAvailable: Boolean,
        @SerializedName("nextStatus") val nextStatus: Int,
        @SerializedName("initialDateAvailability") val initialDateAvailability: Date,
        @SerializedName("finalDateAvailability") val finalDateAvailability: Date,
        @SerializedName("message") val message: String,
        @SerializedName("activeBorrrow") val activeBorrow: Borrow,
        @SerializedName("book") val book: Book
)
