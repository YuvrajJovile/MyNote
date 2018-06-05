package com.mynote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mynote.model.NotesModel;

import java.util.ArrayList;
import java.util.List;

import static com.mynote.utils.Constants.COLUMN_COLOR;
import static com.mynote.utils.Constants.COLUMN_CREATED_OR_MODIFIED;
import static com.mynote.utils.Constants.COLUMN_FAVORITE;
import static com.mynote.utils.Constants.COLUMN_ID;
import static com.mynote.utils.Constants.COLUMN_NOTE_DESCRIPTION;
import static com.mynote.utils.Constants.COLUMN_NOTE_TITLE;
import static com.mynote.utils.Constants.COLUMN_REMAINDER_TIME;
import static com.mynote.utils.Constants.COLUMN_TIMESTAMP;
import static com.mynote.utils.Constants.TABLE_NAME;

public class NotesTable {


    static DatabaseHelper mDBhelper;


   /* public NotesTable() {
        Log.d(this + "", "NotesTable is called");

    }*/

    public NotesTable(Context pContext) {
        mDBhelper = new DatabaseHelper(pContext);
        Log.d(this + "", "Context is set");

    }


    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NOTE_TITLE + " TEXT,"
                    + COLUMN_NOTE_DESCRIPTION + " TEXT,"
                    + COLUMN_TIMESTAMP + " TEXT,"
                    + COLUMN_COLOR + " TEXT,"
                    + COLUMN_CREATED_OR_MODIFIED + " TEXT,"
                    + COLUMN_FAVORITE + " TEXT,"
                    + COLUMN_REMAINDER_TIME + " TEXT"
                    + ")";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }


    public long insertNote(NotesModel pNotesModel) {

        SQLiteDatabase lSqLiteDatabase = mDBhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTE_TITLE, pNotesModel.getTitle());
        values.put(COLUMN_NOTE_DESCRIPTION, pNotesModel.getDescription());
        values.put(COLUMN_TIMESTAMP, pNotesModel.getDate());
        values.put(COLUMN_COLOR, pNotesModel.getColor());
        values.put(COLUMN_CREATED_OR_MODIFIED, pNotesModel.getCreatedOrModified());
        values.put(COLUMN_FAVORITE, pNotesModel.isFavourite() + "");
        values.put(COLUMN_REMAINDER_TIME, pNotesModel.getRemainderTime());

        long id = lSqLiteDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        lSqLiteDatabase.close();
        return id;
    }


    public NotesModel getNote(long id) {


        SQLiteDatabase lDb = mDBhelper.getReadableDatabase();

        String[] lProjection = {
                COLUMN_ID,
                COLUMN_NOTE_TITLE,
                COLUMN_NOTE_DESCRIPTION,
                COLUMN_TIMESTAMP,
                COLUMN_COLOR,
                COLUMN_CREATED_OR_MODIFIED,
                COLUMN_FAVORITE,
                COLUMN_REMAINDER_TIME
        };

        String lSelection = COLUMN_ID + " = ?";
        String[] lSelectionArgs = {id + ""};

        Cursor lCursor = lDb.query(TABLE_NAME,
                lProjection,
                lSelection,
                lSelectionArgs,
                null,
                null,
                null);

        if (lCursor != null)
            lCursor.moveToFirst();

        NotesModel lData = new NotesModel(
                lCursor.getInt(lCursor.getColumnIndex(COLUMN_ID)),
                lCursor.getString(lCursor.getColumnIndex(COLUMN_NOTE_TITLE)),
                lCursor.getString(lCursor.getColumnIndex(COLUMN_NOTE_DESCRIPTION)),
                lCursor.getString(lCursor.getColumnIndex(COLUMN_TIMESTAMP)),
                lCursor.getString(lCursor.getColumnIndex(COLUMN_COLOR)),
                lCursor.getString(lCursor.getColumnIndex(COLUMN_CREATED_OR_MODIFIED)),
                Boolean.parseBoolean(lCursor.getString(lCursor.getColumnIndex(COLUMN_FAVORITE))),
                Long.parseLong(lCursor.getString(lCursor.getColumnIndex(COLUMN_REMAINDER_TIME))));
        lCursor.close();
        lDb.close();

        return lData;
    }


    public List<NotesModel> getAllNotes() {


        List<NotesModel> lNotesModels = new ArrayList<>();

        String lSelectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " +
                COLUMN_TIMESTAMP + " DESC";


        SQLiteDatabase lDb = mDBhelper.getReadableDatabase();
        Cursor lCursor = lDb.rawQuery(lSelectQuery, null);


        if (lCursor.moveToFirst()) {
            do {

                NotesModel lNotesModel = new NotesModel();
                lNotesModel.setId(lCursor.getInt(lCursor.getColumnIndex(COLUMN_ID)));
                lNotesModel.setTitle(lCursor.getString(lCursor.getColumnIndex(COLUMN_NOTE_TITLE)));
                lNotesModel.setDescription(lCursor.getString(lCursor.getColumnIndex(COLUMN_NOTE_DESCRIPTION)));
                lNotesModel.setDate(lCursor.getString(lCursor.getColumnIndex(COLUMN_TIMESTAMP)));
                lNotesModel.setColor(lCursor.getString(lCursor.getColumnIndex(COLUMN_COLOR)));
                lNotesModel.setCreatedOrModified(lCursor.getString(lCursor.getColumnIndex(COLUMN_CREATED_OR_MODIFIED)));
                lNotesModel.setFavourite(Boolean.parseBoolean(lCursor.getString(lCursor.getColumnIndex(COLUMN_FAVORITE))));
                lNotesModel.setRemainderTime(Long.parseLong(lCursor.getString(lCursor.getColumnIndex(COLUMN_REMAINDER_TIME))));

                lNotesModels.add(lNotesModel);
            } while (lCursor.moveToNext());
        }

        lCursor.close();
        lDb.close();

        return lNotesModels;
    }

   /* public int getNotesCount() {

        String queryString = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }*/


    public void upDateNotes(NotesModel pNotesModel) {

        SQLiteDatabase db = mDBhelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTE_TITLE, pNotesModel.getTitle());
        values.put(COLUMN_NOTE_DESCRIPTION, pNotesModel.getDescription());
        values.put(COLUMN_TIMESTAMP, pNotesModel.getDate());
        values.put(COLUMN_COLOR, pNotesModel.getColor());
        values.put(COLUMN_CREATED_OR_MODIFIED, pNotesModel.getCreatedOrModified());
        values.put(COLUMN_FAVORITE, pNotesModel.isFavourite() + "");
        values.put(COLUMN_REMAINDER_TIME, pNotesModel.getRemainderTime());

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
