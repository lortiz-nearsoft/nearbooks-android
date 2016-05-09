package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class VolumeInfo(
        @SerializedName("title") var title: String? = null,
        @SerializedName("subtitle") var subtitle: String? = null,
        @SerializedName("authors") var authors: List<String> = mutableListOf(),
        @SerializedName("publisher") var publisher: String? = null,
        @SerializedName("publishedDate") var publishedDate: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("industryIdentifiers") var industryIdentifiers: List<IndustryIdentifier> = mutableListOf(),
        @SerializedName("readingModes") var readingModes: ReadingModes? = null,
        @SerializedName("pageCount") var pageCount: Int = 0,
        @SerializedName("printType") var printType: String? = null,
        @SerializedName("categories") var categories: List<String> = mutableListOf(),
        @SerializedName("averageRating") var averageRating: Double = 0.toDouble(),
        @SerializedName("ratingsCount") var ratingsCount: Int = 0,
        @SerializedName("maturityRating") var maturityRating: String? = null,
        @SerializedName("allowAnonLogging") var isAllowAnonLogging: Boolean = false,
        @SerializedName("contentVersion") var contentVersion: String? = null,
        @SerializedName("imageLinks") var imageLinks: ImageLinks? = null,
        @SerializedName("language") var language: String? = null,
        @SerializedName("previewLink") var previewLink: String? = null,
        @SerializedName("infoLink") var infoLink: String? = null,
        @SerializedName("canonicalVolumeLink") var canonicalVolumeLink: String? = null
)
