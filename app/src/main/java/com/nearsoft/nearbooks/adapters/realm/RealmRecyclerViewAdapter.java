package com.nearsoft.nearbooks.adapters.realm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Realm recycler view adapter.
 * Created by epool on 11/20/15.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
    private RealmBaseAdapter<T> realmBaseAdapter;

    public RealmRecyclerViewAdapter(Context context, RealmResults<T> realmResults, boolean automaticUpdate) {
        realmBaseAdapter = new SimpleRealmBaseAdapter<>(context, realmResults, automaticUpdate);
    }

    public T getItem(int position) {
        return realmBaseAdapter.getItem(position);
    }

    @Override
    public int getItemCount() {
        return realmBaseAdapter.getCount();
    }

}
