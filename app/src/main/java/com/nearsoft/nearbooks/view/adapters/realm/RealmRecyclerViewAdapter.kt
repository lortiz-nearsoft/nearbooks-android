package com.nearsoft.nearbooks.view.adapters.realm

import android.content.Context
import android.support.v7.widget.RecyclerView

import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import io.realm.RealmObject
import io.realm.RealmResults

/**
 * Realm recycler view adapter.
 * Created by epool on 11/28/15.
 */
abstract class RealmRecyclerViewAdapter<T : RealmObject>(
        context: Context,
        realmResults: RealmResults<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val realmBaseAdapter: RealmBaseAdapter<T>

    init {
        realmBaseAdapter = SimpleRealmBaseAdapter(context, realmResults)
    }

    fun getItem(position: Int): T {
        return realmBaseAdapter.getItem(position)
    }

    override fun getItemCount(): Int {
        return realmBaseAdapter.count
    }

    fun updateData(data: OrderedRealmCollection<T>) {
        realmBaseAdapter.updateData(data)
    }

}
