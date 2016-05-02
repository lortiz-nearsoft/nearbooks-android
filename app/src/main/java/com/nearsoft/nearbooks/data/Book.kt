package com.nearsoft.nearbooks.data

import com.google.gson.annotations.SerializedName

/**
 * Book data class.
 * Created by epool on 5/1/16.
 */
data class Book(
        @SerializedName("bookID") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("author") val author: String,
        @SerializedName("releaseYear") val releaseYear: String,
        @SerializedName("description") val description: String,
        @SerializedName("copies") val numberOfCopies: Int,
        @SerializedName("days") val numberOfDaysAllowedForBorrowing: Int,
        @SerializedName("isAvailable") val isAvailable: Boolean,
        @SerializedName("borrows") val borrows: List<Borrow>
)