package com.mynote.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mynote.R;
import com.mynote.database.NotesTable;
import com.mynote.database.model.NotesModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.mynote.utils.IConstants.CREATE;
import static com.mynote.utils.IConstants.DATA_DATE;
import static com.mynote.utils.IConstants.DATA_DES;
import static com.mynote.utils.IConstants.DATA_ID;
import static com.mynote.utils.IConstants.DATA_TITLE;
import static com.mynote.utils.IConstants.DELETE;
import static com.mynote.utils.IConstants.EDIT;
import static com.mynote.utils.IConstants.EDIT_OR_CREATE_OR_DELETE;

public class AddNotesActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btSave;
    private Toast mToast;
    private long mID = -1;
    private ProgressBar mProgressBar;


    private String flagEditOrCreate = null;
    private NotesTable mNotesTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        mToast = new Toast(this);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_discription);
        btSave = findViewById(R.id.bt_save);
        mProgressBar = findViewById(R.id.v_progress);

        mNotesTable = new NotesTable(this);


        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            flagEditOrCreate = bundle.getString(EDIT_OR_CREATE_OR_DELETE);


            if (flagEditOrCreate.equals(EDIT)) {
                etTitle.setText(bundle.getString(DATA_TITLE));
                etDescription.setText(bundle.getString(DATA_DES));
                mID = Integer.parseInt(bundle.getString(DATA_ID));
                Log.d(this + "", "id==" + mID);
            }
        }

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {

                    mProgressBar.setVisibility(View.VISIBLE);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMM:yyyy \nhh:mm:ss a");

                    String title = etTitle.getText().toString();
                    String des = etDescription.getText().toString();
                    String timeStamp = simpleDateFormat.format(new Date());
                    String createOrEdit = CREATE;


                    NotesModel notesModel = new NotesModel(mID, title, des, timeStamp);


                    Intent intent = new Intent();
                    Bundle bundleReturn = new Bundle();


                    if (flagEditOrCreate.equals(EDIT)) {
                        new PerformUpdate().doInBackground(notesModel);
                        createOrEdit = EDIT;
                    } else {
                        mID = new PerformInsert().doInBackground(notesModel);
                    }


                    mProgressBar.setVisibility(View.GONE);


                    Log.d(this + "", "id==" + mID);


                    bundleReturn.putString(EDIT_OR_CREATE_OR_DELETE, createOrEdit);
                    bundleReturn.putString(DATA_ID, mID + "");
                    bundleReturn.putString(DATA_TITLE, title);
                    bundleReturn.putString(DATA_DES, des);
                    bundleReturn.putString(DATA_DATE, timeStamp);
                    intent.putExtras(bundleReturn);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_delete:
                if (!flagEditOrCreate.equals(CREATE))
                    performDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void performDelete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddNotesActivity.this);
        builder.setMessage(R.string.delete)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PerformDelete().doInBackground();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        builder.show();
    }


    private boolean validate() {

        if (etTitle.getText().toString().length() <= 0) {
            showMessage("Enter a Title");
            return false;
        } else if (etDescription.getText().toString().length() <= 0) {
            showMessage("Enter a description");
            return false;
        }
        return true;
    }

    private void showMessage(String pData) {


        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, pData, Toast.LENGTH_SHORT);
        mToast.show();
    }


    class PerformDelete extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mID != -1) {
                mNotesTable.deleteNotes(mID);
                navigateToMain();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    class PerformUpdate extends AsyncTask<NotesModel, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(NotesModel... notesModels) {

            mNotesTable.upDateNotes(notesModels[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    class PerformInsert extends AsyncTask<NotesModel, Void, Long> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Long doInBackground(NotesModel... notesModels) {
            return mNotesTable.insertNote(notesModels[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void navigateToMain() {

        Intent intent = new Intent();
        Bundle bundleReturn = new Bundle();
        bundleReturn.putString(EDIT_OR_CREATE_OR_DELETE, DELETE);
        intent.putExtras(bundleReturn);
        setResult(RESULT_OK, intent);
        finish();
    }
}
