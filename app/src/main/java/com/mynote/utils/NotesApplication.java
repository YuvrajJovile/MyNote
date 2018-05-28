package com.mynote.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import static com.mynote.utils.Constants.IN_OUT_KEY;

public class NotesApplication extends Application {


    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private static NotesApplication mNotesApplication;
    private static String SHARED_KEY = "com.notes";

    @Override
    public void onCreate() {
        super.onCreate();
        mNotesApplication = this;

        mSharedPreferences = getSharedPreferences(
                SHARED_KEY, Context.MODE_PRIVATE);

    }

    public static NotesApplication getInstance() {


        return mNotesApplication;
    }


    public void setLoginFlag(String pLoginFlag) {
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mSharedPreferencesEditor.putString(IN_OUT_KEY, pLoginFlag);
        mSharedPreferencesEditor.commit();
    }

    public String getLoginDetails() {
        return mSharedPreferences.getString(IN_OUT_KEY, "");

    }
}
