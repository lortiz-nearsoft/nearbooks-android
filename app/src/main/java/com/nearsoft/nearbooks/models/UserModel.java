package com.nearsoft.nearbooks.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.nearsoft.nearbooks.R;

/**
 * User model.
 * Created by epool on 11/18/15.
 */
public class UserModel {

    public static void signIn(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_file_name), Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(context.getString(R.string.current_user_id), userId)
                .apply();
    }

    public static String getCurrentUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_file_name), Context.MODE_PRIVATE);
        return sharedPreferences.getString(context.getString(R.string.current_user_id), null);
    }

    public static void signOut(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_file_name), Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(context.getString(R.string.current_user_id), null)
                .apply();
    }

}
