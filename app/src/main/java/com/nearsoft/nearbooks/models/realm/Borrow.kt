package com.nearsoft.nearbooks.models.realm

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Realm borrow model.
 * Created by epool on 5/1/16.
 */
open class Borrow : RealmObject() {

    @PrimaryKey
    @SerializedName("borrowID")
    var id: String? = null

    @SerializedName("bookID")
    var bookId: String? = null

    @SerializedName("copyNumber")
    var copyNumber: Int = 0

    @SerializedName("userEmail")
    var userEmail: String? = null

    @SerializedName("status")
    var status: Int = 0

    @SerializedName("initialDate")
    var initialDate: Date? = null

    @SerializedName("finalDate")
    var finalDate: Date? = null

    @SerializedName("checkIn")
    var checkInDate: Date? = null

    @SerializedName("checkOut")
    var checkOutDate: Date? = null

}
