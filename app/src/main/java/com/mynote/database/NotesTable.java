package com.mynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mynote.database.model.NotesModel;

import java.util.ArrayList;
import java.util.List;

import static com.mynote.utils.Constants.COLUMN_COLOR;
import static com.mynote.utils.Constants.COLUMN_CREATED_OR_MODIFIED;
import static com.mynote.utils.Constants.COLUMN_ID;
import static com.mynote.utils.Constants.COLUMN_NOTE_DESRIPTION;
import static com.mynote.utils.Constants.COLUMN_NOTE_TITLE;
import static com.mynote.utils.Constants.COLUMN_TIMESTAMP;
import static com.mynote.utils.Constants.TABLE_NAME;

public class NotesTable {


    static DatabaseHelper mDBhelper;


    public NotesTable() {
        Log.d(this + "", "NotesTable is called");

    }

    public NotesTable(Context pContext) {
        mDBhelper = new DatabaseHelper(pContext);
        Log.d(this + "", "Context is set");

    }


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE_TITLE + " TEXT,"
                    + COLUMN_NOTE_DESRIPTION + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT,"
                    + COLUMN_COLOR + " TEXT,"
                    + COLUMN_CREATED_OR_MODIFIED + " TEXT"
                    + ")";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }


    public long insertNote(NotesModel pNotesModel) {

        SQLiteDatabase lSqLiteDatabase = mDBhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTE_TITLE, pNotesModel.getTitle());
        values.put(COLUMN_NOTE_DESRIPTION, pNotesModel.getDescription());
        values.put(COLUMN_TIMESTAMP, pNotesModel.getDate());
        values.put(COLUMN_COLOR, pNotesModel.getColor());
        values.put(COLUMN_CREATED_OR_MODIFIED, pNotesModel.getCreatedOrModified());

        long id = lSqLiteDatabase.insert(TABLE_NAME, null, values);
        lSqLiteDatabase.close();
        return id;
    }


    public NotesModel getNote(long id) {


        SQLiteDatabase db = mDBhelper.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_NOTE_TITLE,
                COLUMN_NOTE_DESRIPTION,
                COLUMN_TIMESTAMP,
                COLUMN_COLOR,
                COLUMN_CREATED_OR_MODIFIED
        };

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id + ""};

        Cursor cursor = db.query(TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor != null)
            cursor.moveToFirst();

        NotesModel lData = new NotesModel(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_DESRIPTION)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COLOR)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_OR_MODIFIED)));
        cursor.close();


        return lData;
    }


    public List<NotesModel> getAllNotes() {


        List<NotesModel> lNotesModels = new ArrayList<>();

        String sellectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " +
                COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sellectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                NotesModel lNotesModel = new NotesModel();
                lNotesModel.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                lNotesModel.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                lNotesModel.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_DESRIPTION)));
                lNotesModel.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                lNotesModel.setColor(cursor.getString(cursor.getColumnIndex(COLUMN_COLOR)));
                lNotesModel.setCreatedOrModified(cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_OR_MODIFIED)));

                lNotesModels.add(lNotesModel);
            } while (cursor.moveToNext());
        }

        db.close();

        return lNotesModels;
    }

    public int getNotesCount() {

        String queryString = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }


    public void upDateNotes(NotesModel pNotesModel) {

        SQLiteDatabase db = mDBhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, pNotesModel.getTitle());
        values.put(COLUMN_NOTE_DESRIPTION, pNotesModel.getDescription());
        values.put(COLUMN_TIMESTAMP, pNotesModel.getDate());
        values.put(COLUMN_COLOR, pNotesModel.getColor());
        values.put(COLUMN_CREATED_OR_MODIFIED, pNotesModel.getCreatedOrModified());

        String selection = COLUMN_ID + " = ? ";
        String[] selectionArgs = {pNotesModel.getId() + ""};
        db.update(TABLE_NAME, values, selection, selectionArgs);
        db.close();


    }

    public void deleteNotes(long pId) {
        SQLiteDatabase db = mDBhelper.getWritableDatabase();

        String selection = COLUMN_ID + " = ? ";
        String[] selectionArgs = {pId + ""};

        db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }


}
