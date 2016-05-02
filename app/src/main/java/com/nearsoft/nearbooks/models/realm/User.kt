package com.nearsoft.nearbooks.models.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Realm user model.
 * Created by epool on 5/1/16.
 */
open class User() : RealmObject() {

    @PrimaryKey
    var id: String? = null

    var displayName: String? = null

    var email: String? = null

    var photoUrl: String? = null

    var idToken: String? = null

    constructor(user: com.nearsoft.nearbooks.models.view.User) : this() {
        id = user.id
        displayName = user.displayName;
        email = user.email;
        idToken = user.idToken;
        photoUrl = user.photoUrl;
    }

}
