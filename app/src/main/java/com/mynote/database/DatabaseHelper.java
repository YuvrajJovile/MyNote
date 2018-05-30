package com.mynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.mynote.utils.Constants.DATABASE_NAME;
import static com.mynote.utils.Constants.DATABASE_VERSION;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase pDb) {

        Log.d(this + "", "onCreate is called");
        NotesTable.createTable(pDb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDb, int pOldVersion, int pNewVersion) {

        pDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(pDb);

    }
}
