package com.mynote.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mynote.R;
import com.mynote.database.NotesTable;
import com.mynote.database.model.NotesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.mynote.utils.Constants.CHORD_COLOR;
import static com.mynote.utils.Constants.COLOR_BLUE;
import static com.mynote.utils.Constants.COLOR_DEFAULT;
import static com.mynote.utils.Constants.COLOR_GREEN;
import static com.mynote.utils.Constants.COLOR_GREEN_LIGHT;
import static com.mynote.utils.Constants.COLOR_ORANGE;
import static com.mynote.utils.Constants.COLOR_PINK;
import static com.mynote.utils.Constants.COLOR_VIOLET;
import static com.mynote.utils.Constants.COLUMN_CREATED_OR_MODIFIED;
import static com.mynote.utils.Constants.CREATE;
import static com.mynote.utils.Constants.CREATED;
import static com.mynote.utils.Constants.DATA_DATE;
import static com.mynote.utils.Constants.DATA_DES;
import static com.mynote.utils.Constants.DATA_ID;
import static com.mynote.utils.Constants.DATA_TITLE;
import static com.mynote.utils.Constants.DELETE;
import static com.mynote.utils.Constants.EDIT;
import static com.mynote.utils.Constants.EDIT_OR_CREATE_OR_DELETE;
import static com.mynote.utils.Constants.MODIFIED;

public class AddNotesActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;

    private ImageButton mIbDelete, mIbFavorites, mIbRemainder, mIbShare;

    private TextView mtvDateModified;
    private Toast mToast;
    private long mID = -1;
    private ProgressBar mProgressBar;
    private ImageButton mIbBack;

    private RadioGroup mRgColorGroup;


    private String flagEditOrCreate = null;
    private NotesTable mNotesTable;

    private boolean flagSwitchFavorites = true;
    private boolean flagSwitchRemainder = true;

    private String mChordColor = COLOR_DEFAULT;
    private String mCreatedOrModified = CREATED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        mToast = new Toast(this);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        mProgressBar = findViewById(R.id.v_progress);
        mtvDateModified = findViewById(R.id.tv_date_modified);
        mIbBack = findViewById(R.id.ib_back);
        mNotesTable = new NotesTable(this);


        mIbDelete = findViewById(R.id.ib_delete);
        mIbFavorites = findViewById(R.id.ib_favourites);
        mIbRemainder = findViewById(R.id.ib_remainder);
        mIbShare = findViewById(R.id.ib_share);

        mRgColorGroup = findViewById(R.id.rg_color_group);

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            flagEditOrCreate = bundle.getString(EDIT_OR_CREATE_OR_DELETE);


            if (flagEditOrCreate.equals(EDIT)) {

                String titleString = bundle.getString(DATA_TITLE);
                if (titleString.isEmpty() || titleString.length() == 0 || titleString.equals(""))
                    titleString = "No Title";
                etTitle.setText(titleString);
                etDescription.setText(bundle.getString(DATA_DES));
                mChordColor = bundle.getString(CHORD_COLOR);
                mCreatedOrModified = bundle.getString(COLUMN_CREATED_OR_MODIFIED);

                int id = -1;

                if (mChordColor.equals(COLOR_DEFAULT)) {
                    id = 0;
                } else if (mChordColor.equals(COLOR_GREEN)) {
                    id = R.id.rb_green;
                } else if (mChordColor.equals(COLOR_GREEN_LIGHT)) {
                    id = R.id.rb_green_light;
                } else if (mChordColor.equals(COLOR_BLUE)) {
                    id = R.id.rb_color_blue;
                } else if (mChordColor.equals(COLOR_VIOLET)) {
                    id = R.id.rb_color_violet;
                } else if (mChordColor.equals(COLOR_PINK)) {
                    id = R.id.rb_color_pink;
                } else if (mChordColor.equals(COLOR_ORANGE)) {
                    id = R.id.rb_color_orange;
                }


                if (id != 0)
                    mRgColorGroup.check(id);

                try {
                    etDescription.setBackgroundColor(Color.parseColor(mChordColor));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String dateString = bundle.getString(DATA_DATE);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM \thh:mm a");
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd:MMM:yyyy \nhh:mm:ss a");
                try {
                    Date date = inputFormat.parse(dateString);
                    dateString = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String modifiedTitle = ((mCreatedOrModified.equals(MODIFIED)) ? getString(R.string.last_modified) : getString(R.string.created_at)) + dateString;
                mtvDateModified.setText(modifiedTitle);
                mID = Integer.parseInt(bundle.getString(DATA_ID));
                Log.d(this + "", "id==" + mID);
            }
        }


        mIbDelete.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                performDelete();
            }
        });

        mIbShare.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                performShare();
            }
        });

        mIbFavorites.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                performOnCLickFavorites();
            }
        });

        mIbRemainder.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                performOnClickRemainder();
            }
        });

        mRgColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                switch (checkedId) {
                    case R.id.rb_green:

                        mChordColor = COLOR_GREEN;
                        break;
                    case R.id.rb_green_light:

                        mChordColor = COLOR_GREEN_LIGHT;
                        break;
                    case R.id.rb_color_blue:

                        mChordColor = COLOR_BLUE;
                        break;
                    case R.id.rb_color_violet:

                        mChordColor = COLOR_VIOLET;
                        break;
                    case R.id.rb_color_pink:

                        mChordColor = COLOR_PINK;
                        break;
                    case R.id.rb_color_orange:

                        mChordColor = COLOR_ORANGE;
                        break;
                }
                try {
                    etDescription.setBackgroundColor(Color.parseColor(mChordColor));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void performOnClickRemainder() {

        if (flagSwitchRemainder) {
            mIbRemainder.setSelected(true);
        } else {
            mIbRemainder.setSelected(false);
        }

        flagSwitchRemainder = flagSwitchRemainder != true;
    }

    private void performOnCLickFavorites() {

        if (flagSwitchFavorites) {
            mIbFavorites.setSelected(true);
        } else {
            mIbFavorites.setSelected(false);
        }

        flagSwitchFavorites = flagSwitchFavorites != true;

    }

    private void performShare() {

        String dataToShare = getString(R.string.share_title) + etTitle.getText().toString() + getString(R.string.share_description) + etDescription.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }


    @Override
    public void onBackPressed() {

        if (validate()) {

            mProgressBar.setVisibility(View.VISIBLE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMM:yyyy \nhh:mm:ss a");


            String title = etTitle.getText().toString();
            title = title.isEmpty() || title.equals("") ? getString(R.string.no_title) : title;
            String des = etDescription.getText().toString();
            String timeStamp = simpleDateFormat.format(new Date());
            String createOrEdit = CREATE;


            NotesModel notesModel;


            Intent intent = new Intent();
            Bundle bundleReturn = new Bundle();


            if (flagEditOrCreate.equals(EDIT)) {
                mCreatedOrModified = MODIFIED;
                notesModel = new NotesModel(mID, title, des, timeStamp, mChordColor, mCreatedOrModified);
                new PerformUpdate().doInBackground(notesModel);
                createOrEdit = EDIT;
            } else {
                mCreatedOrModified = CREATED;
                notesModel = new NotesModel(mID, title, des, timeStamp, mChordColor, mCreatedOrModified);
                mID = new PerformInsert().doInBackground(notesModel);
            }


            mProgressBar.setVisibility(View.GONE);


            Log.d(this + "", "id==" + mID);


            bundleReturn.putString(EDIT_OR_CREATE_OR_DELETE, createOrEdit);
            bundleReturn.putString(DATA_ID, mID + "");
            bundleReturn.putString(DATA_TITLE, title);
            bundleReturn.putString(DATA_DES, des);
            bundleReturn.putString(DATA_DATE, timeStamp);
            bundleReturn.putString(CHORD_COLOR, mChordColor);
            bundleReturn.putString(COLUMN_CREATED_OR_MODIFIED, mCreatedOrModified);
            intent.putExtras(bundleReturn);
            setResult(RESULT_OK, intent);
            finish();
        }

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

        if (etDescription.getText().toString().length() <= 0) {
            showMessage(getString(R.string.enter_a_description));
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
