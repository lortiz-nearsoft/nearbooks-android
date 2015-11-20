package com.nearsoft.nearbooks.view.realm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Realm model adapter.
 * Created by epool on 11/20/15.
 */
public class SimpleRealmBaseAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {

    public SimpleRealmBaseAdapter(Context context, RealmResults<T> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
