package com.nearsoft.nearbooks.models.realm

import com.google.gson.annotations.SerializedName

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Realm book model.
 * Created by epool on 5/1/16.
 */
open class Book : RealmObject() {

    companion object {
        const val ID = "id"
        const val TITLE = "title"
        const val AUTHOR = "id"
        const val RELEASE_YEAR = "id"
    }

    @PrimaryKey
    @SerializedName("bookID")
    var id: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("author")
    var author: String? = null

    @SerializedName("releaseYear")
    var releaseYear: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("copies")
    var numberOfCopies: Int = 0

    @SerializedName("days")
    var numberOfDaysAllowedForBorrowing: Int = 0

    @SerializedName("isAvailable")
    var isAvailable: Boolean = false

    @SerializedName("borrows")
    var borrows: RealmList<Borrow>? = null

}
