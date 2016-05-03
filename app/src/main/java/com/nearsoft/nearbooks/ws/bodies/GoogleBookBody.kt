package com.nearsoft.nearbooks.ws.bodies

import com.google.gson.annotations.SerializedName
import com.nearsoft.nearbooks.ws.responses.googlebooks.Volume

/**
 * Body to create a new book on the server.
 * Created by epool on 2/2/16.
 */
data class GoogleBookBody(
        @SerializedName("isbn") var isbn: String?,
        private val volume: Volume,
        @SerializedName("title") var title: String? = volume.volumeInfo?.title,
        @SerializedName("authors") var authors: List<String>? = volume.volumeInfo?.authors,
        @SerializedName("publishedDate") var publishedDate: String? = volume.volumeInfo?.publishedDate,
        @SerializedName("description") var description: String? = volume.volumeInfo?.description,
        @SerializedName("thumbnailImageUrl") var thumbnailImageUrl: String? = volume.volumeInfo?.imageLinks?.thumbnail
)
