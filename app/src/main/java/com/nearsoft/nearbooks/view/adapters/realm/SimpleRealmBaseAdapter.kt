package com.nearsoft.nearbooks.view.adapters.realm

import android.content.Context
import android.view.View
import android.view.ViewGroup

import io.realm.RealmBaseAdapter
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Realm model adapter.
 * Created by epool on 11/28/15.
 */
class SimpleRealmBaseAdapter<T : RealmObject>(
        context: Context,
        realmResults: RealmResults<T>
) : RealmBaseAdapter<T>(context, realmResults) {

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View? {
        return null
    }

}
