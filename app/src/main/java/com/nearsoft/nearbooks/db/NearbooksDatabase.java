package com.nearsoft.nearbooks.db;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Database placeholder.
 * Created by epool on 12/17/15.
 */
@ContentProvider(
        authority = NearbooksDatabase.CONTENT_AUTHORITY,
        database = NearbooksDatabase.class,
        baseContentUri = ContentUtils.BASE_CONTENT_URI
)
@Database(name = NearbooksDatabase.NAME, version = NearbooksDatabase.VERSION)
public class NearbooksDatabase {

    public static final String NAME = "nearbooks";
    public static final int VERSION = 1;

    // Must be the class name whose implements the content provider annotation.
    public static final String CONTENT_PROVIDER_NAME = "NearbooksDatabase";
    public static final String CONTENT_AUTHORITY = "com.nearsoft.nearbooks.provider";

}
