package com.nearsoft.nearbooks.data

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Borrow data class.
 * Created by epool on 5/1/16.
 */
data class Borrow(
        @SerializedName("borrowID") val id: String,
        @SerializedName("bookID") val bookId: String,
        @SerializedName("copyNumber") val copyNumber: Int,
        @SerializedName("userEmail") val userEmail: String,
        @SerializedName("status") val status: Int,
        @SerializedName("initialDate") val initialDate: Date,
        @SerializedName("finalDate") val finalDate: Date,
        @SerializedName("checkIn") val checkInDate: Date,
        @SerializedName("checkOut") val checkOutDate: Date
)