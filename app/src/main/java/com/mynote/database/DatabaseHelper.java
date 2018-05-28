package com.mynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.mynote.utils.Constants.MAIN_DATABASE_NAME;
import static com.mynote.utils.Constants.MAIN_DATABASE_VERSION;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, MAIN_DATABASE_NAME, null, MAIN_DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(this + "", "onCreate is called");
        NotesTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MAIN_DATABASE_NAME);
        onCreate(db);

    }
}
