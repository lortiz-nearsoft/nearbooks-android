package com.nearsoft.nearbooks.data

import com.google.gson.annotations.SerializedName

/**
 * User data class.
 * Created by epool on 5/1/16.
 */
data class User(
        @SerializedName("id") val id: String,
        @SerializedName("displayName") val displayName: String,
        @SerializedName("email") val email: String,
        @SerializedName("photoUrl") val photoUrl: String,
        @SerializedName("idToken") val idToken: String
)