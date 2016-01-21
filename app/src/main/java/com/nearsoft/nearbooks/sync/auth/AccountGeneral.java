package com.nearsoft.nearbooks.sync.auth;

/**
 * Account general.
 * Created by epool on 12/21/15.
 */
public class AccountGeneral {

    // Value below must match the account type specified in res/xml/sync_adapter.xml
    public static final String ACCOUNT_TYPE = "com.nearsoft.nearbooks.account";

    public static final long SYNC_FREQUENCY = 60 * 60;  // 1 hour (in seconds)
    public static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static final String AUTH_TOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTH_TOKEN_TYPE_READ_ONLY_LABEL =
            "Read only access to an Nearsoft account";

    public static final String AUTH_TOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTH_TOKEN_TYPE_FULL_ACCESS_LABEL =
            "Full access to an Nearsoft account";

}
