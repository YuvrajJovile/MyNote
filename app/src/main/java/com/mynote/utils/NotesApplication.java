package com.mynote.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import static com.mynote.utils.IConstants.IN_OUT_KEY;

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


    public void setLoginFlag(boolean pLoginFlag) {
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mSharedPreferencesEditor.putBoolean(IN_OUT_KEY, pLoginFlag);
        mSharedPreferencesEditor.commit();
    }

    public boolean getLoginDetails() {
        return mSharedPreferences.getBoolean(IN_OUT_KEY, true);
    }
}
