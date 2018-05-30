package com.mynote.view;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mynote.R;
import com.mynote.broadcast.RemainderBroadcast;
import com.mynote.database.NotesTable;
import com.mynote.database.model.NotesModel;
import com.mynote.utils.RingToneManagerClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import static com.mynote.utils.Constants.COLUMN_FAVORITE;
import static com.mynote.utils.Constants.COLUMN_REMAINDER_TIME;
import static com.mynote.utils.Constants.CREATE;
import static com.mynote.utils.Constants.CREATED;
import static com.mynote.utils.Constants.DATA_DATE;
import static com.mynote.utils.Constants.DATA_DES;
import static com.mynote.utils.Constants.DATA_ID;
import static com.mynote.utils.Constants.DATA_TITLE;
import static com.mynote.utils.Constants.DELETE;
import static com.mynote.utils.Constants.EDIT;
import static com.mynote.utils.Constants.EDIT_OR_CREATE_OR_DELETE;
import static com.mynote.utils.Constants.LANDSCAPE;
import static com.mynote.utils.Constants.MODIFIED;
import static com.mynote.utils.Constants.POTRAIT;

public class AddNotesActivity extends AppCompatActivity {

    private EditText mEtTitle, mEtDescription;

    private ImageButton mIbDelete, mIbFavorites, mIbRemainder, mIbShare;

    private TextView mtvDateModified, mtvRemainder;
    private Toast mToast;
    private long mID = -1;
    private ProgressBar mProgressBar;
    private ImageButton mIbBack;

    private RadioGroup mRgColorGroup;


    private String mFlagEditOrCreate = null;
    private NotesTable mNotesTable;

    private boolean mFlagSwitchFavorites = false;
    private boolean mFlagSwitchRemainder = false;

    private String mChordColor = COLOR_DEFAULT;
    private String mCreatedOrModified = CREATED;

    private Calendar mAlarmCalandar;
    private boolean mFlagDateSelected = false;
    private boolean mFlagTimeSelected = false;


    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;

    private long mRemainderTime = -1;

    private boolean mRemainderSet = false;

    private boolean mFlagChangesMade = false;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd:MMM:yyyy hh:mm:ss a");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        mToast = new Toast(this);
        mEtTitle = findViewById(R.id.et_title);
        mEtDescription = findViewById(R.id.et_description);
        mProgressBar = findViewById(R.id.v_progress);
        mtvDateModified = findViewById(R.id.tv_date_modified);
        mtvRemainder = findViewById(R.id.tv_remainder);
        mIbBack = findViewById(R.id.ib_back);
        mIbDelete = findViewById(R.id.ib_delete);
        mIbFavorites = findViewById(R.id.ib_favourites);
        mIbRemainder = findViewById(R.id.ib_remainder);
        mIbShare = findViewById(R.id.ib_share);
        mRgColorGroup = findViewById(R.id.rg_color_group);

        init();

    }

    private void init() {

        mAlarmCalandar = Calendar.getInstance();

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mNotesTable = new NotesTable(this);

        final Bundle lBundle = getIntent().getExtras();
        if (lBundle != null) {

            mFlagEditOrCreate = lBundle.getString(EDIT_OR_CREATE_OR_DELETE);


            if (mFlagEditOrCreate.equals(EDIT)) {

                mIbDelete.setVisibility(View.VISIBLE);

                String lTitleString = lBundle.getString(DATA_TITLE);
                if (lTitleString.isEmpty() || lTitleString.length() == 0 || lTitleString.equals(""))
                    lTitleString = "No Title";
                mEtTitle.setText(lTitleString);
                mEtDescription.setText(lBundle.getString(DATA_DES));
                mChordColor = lBundle.getString(CHORD_COLOR);
                mCreatedOrModified = lBundle.getString(COLUMN_CREATED_OR_MODIFIED);
                mFlagSwitchFavorites = Boolean.parseBoolean(lBundle.getString(COLUMN_FAVORITE));


                mRemainderTime = Long.parseLong(lBundle.getString(COLUMN_REMAINDER_TIME));

                if (mRemainderTime < mAlarmCalandar.getTimeInMillis()) {
                    mRemainderTime = -1;
                    mFlagSwitchRemainder = false;
                    mRemainderSet = false;

                } else {
                    mRemainderSet = true;
                    mFlagSwitchRemainder = true;
                    mtvRemainder.setVisibility(View.VISIBLE);
                    mAlarmCalandar.setTimeInMillis(mRemainderTime);

                    int hour = mAlarmCalandar.get(Calendar.HOUR);
                    hour = (hour == 0) ? 12 : hour;
                    String lRemainderText = "Remainder set on: " + mAlarmCalandar.get(Calendar.DAY_OF_MONTH) + "/" + (mAlarmCalandar.get(Calendar.MONTH) + 1)
                            + "/" + mAlarmCalandar.get(Calendar.YEAR) + "\t" + hour + ":" + mAlarmCalandar.get(Calendar.MINUTE);
                    mtvRemainder.setText(lRemainderText);

                }

                if (mFlagSwitchFavorites) {
                    mIbFavorites.setSelected(true);
                } else {
                    mIbFavorites.setSelected(false);
                }

                if (mFlagSwitchRemainder) {
                    mIbRemainder.setSelected(true);
                } else {
                    mIbRemainder.setSelected(false);
                }

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


                if (id != 0 || id != -1)
                    mRgColorGroup.check(id);


                String lDateString = lBundle.getString(DATA_DATE);
                SimpleDateFormat lOutputFormat = new SimpleDateFormat("dd MMM \thh:mm a");

                try {
                    Date lDate = mSimpleDateFormat.parse(lDateString);
                    lDateString = lOutputFormat.format(lDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String lModifiedTitle = ((mCreatedOrModified.equals(MODIFIED)) ? getString(R.string.last_modified) : getString(R.string.created_at)) + lDateString;
                mtvDateModified.setText(lModifiedTitle);
                mID = Integer.parseInt(lBundle.getString(DATA_ID));
                Log.d(this + "", "id==" + mID);
            }


            if (mRemainderTime != -1) {
                Intent intent = new Intent(this, RemainderBroadcast.class);
                mPendingIntent = PendingIntent.getBroadcast(this, (int) mID, intent, 0);
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
                if (validate())
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


                if (mRemainderSet) {
                    ShowRemaiderEditDialog();
                } else if (validate())
                    performOnClickRemainder();

            }
        });

        mRgColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                mFlagChangesMade = true;

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


            }
        });


        mEtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFlagChangesMade = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void ShowRemaiderEditDialog() {

        AlertDialog.Builder lDialog = new AlertDialog.Builder(this);
        lDialog.setTitle("Edit/Delete Remainder");
        lDialog.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performOnClickRemainder();

            }
        })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelRemainder();
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        lDialog.show();
    }

    private void cancelRemainder() {
        RingToneManagerClass.getInstance().stopAlarm();

        if (mPendingIntent != null) {
            mAlarmManager.cancel(mPendingIntent);
            mtvRemainder.setVisibility(View.GONE);
            mFlagSwitchRemainder = false;
            mIbRemainder.setSelected(false);
            mRemainderSet = false;

            mRemainderTime = -1;
            mFlagChangesMade = true;


            showMessage(getString(R.string.remainder_canceled));

        }
    }

    @Override
    protected void onResume() {
        //setOrientation();
        super.onResume();
    }

    private void setOrientation() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == POTRAIT) {
        } else if (orientation == LANDSCAPE) {
        }
    }

    private void performOnClickRemainder() {


        TimePickerDialog.OnTimeSetListener lOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                mAlarmCalandar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mAlarmCalandar.set(Calendar.MINUTE, minute);
                mFlagTimeSelected = true;

                setOrEditAlarm(mAlarmCalandar);
            }
        };
        final TimePickerDialog lTimePickerDialog = new TimePickerDialog(this, lOnTimeSetListener, mAlarmCalandar.get(Calendar.HOUR_OF_DAY), mAlarmCalandar.get(Calendar.MINUTE), false);


        DatePickerDialog.OnDateSetListener lOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                mAlarmCalandar.set(Calendar.YEAR, year);
                mAlarmCalandar.set(Calendar.MONTH, month);
                mAlarmCalandar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mFlagDateSelected = true;
                lTimePickerDialog.show();
            }
        };


        if (mFlagDateSelected && !mFlagTimeSelected) {
            mAlarmCalandar = Calendar.getInstance();
            mFlagDateSelected = false;
        }

        DatePickerDialog lDatePickerDialog = new DatePickerDialog(this, lOnDateSetListener, mAlarmCalandar.get(Calendar.YEAR), mAlarmCalandar.get(Calendar.MONTH), mAlarmCalandar.get(Calendar.DAY_OF_MONTH));
        lDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        lDatePickerDialog.show();


    }


    private void setOrEditAlarm(Calendar pAlarmCalendar) {

        mFlagChangesMade = true;
        String lTitle = mEtTitle.getText().toString().isEmpty() ? getString(R.string.no_title) : mEtTitle.getText().toString();
        String lDes = mEtDescription.getText().toString();


        String lTimeStamp = mSimpleDateFormat.format(new Date());

        if (mFlagEditOrCreate.equals(EDIT))
            mCreatedOrModified = MODIFIED;


        mRemainderTime = pAlarmCalendar.getTimeInMillis();
        NotesModel lNotesModel = new NotesModel(mID, lTitle, lDes, lTimeStamp, mChordColor, mCreatedOrModified, mFlagSwitchFavorites, mRemainderTime);

        new PerformUpdate().doInBackground(lNotesModel);
        mtvRemainder.setVisibility(View.VISIBLE);


        int hour = pAlarmCalendar.get(Calendar.HOUR);
        hour = (hour == 0) ? 12 : hour;

        String lRemainderText = "Remainder set on: " + pAlarmCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (pAlarmCalendar.get(Calendar.MONTH) + 1)
                + "/" + pAlarmCalendar.get(Calendar.YEAR) + "\t" + hour + ":" + pAlarmCalendar.get(Calendar.MINUTE);
        mtvRemainder.setText(lRemainderText);


        if (mPendingIntent != null) {
            mAlarmManager.cancel(mPendingIntent);
        }

        Intent lIntent = new Intent(this, RemainderBroadcast.class);
        lIntent.putExtra("id", mID);
        mPendingIntent = PendingIntent.getBroadcast(this, (int) mID, lIntent, 0);
        mAlarmManager.set(AlarmManager.RTC, pAlarmCalendar.getTimeInMillis(), mPendingIntent);
        mRemainderSet = true;
        showMessage(getString(R.string.remainder_set));
        mIbRemainder.setSelected(true);
    }

    private void performOnCLickFavorites() {

        mFlagChangesMade = true;
        if (mIbFavorites.isSelected()) {
            mIbFavorites.setSelected(false);
            mFlagSwitchFavorites = false;
        } else {
            mIbFavorites.setSelected(true);
            mFlagSwitchFavorites = true;
        }


    }

    private void performShare() {

        String lTitle = mEtTitle.getText().toString();
        lTitle = (lTitle.isEmpty() || lTitle == null) ? getString(R.string.no_title) : lTitle;
        String lDataToShare = getString(R.string.share_title) + lTitle + getString(R.string.share_description) + mEtDescription.getText().toString();
        Intent lSendIntent = new Intent();
        lSendIntent.setAction(Intent.ACTION_SEND);
        lSendIntent.putExtra(Intent.EXTRA_TEXT, lDataToShare);
        lSendIntent.setType("text/plain");
        startActivity(lSendIntent);

    }


    @Override
    public void onBackPressed() {

        if (validate() && mFlagChangesMade) {

            mProgressBar.setVisibility(View.VISIBLE);


            String lTitle = mEtTitle.getText().toString();
            lTitle = lTitle.isEmpty() || lTitle.equals("") ? getString(R.string.no_title) : lTitle;
            String lDes = mEtDescription.getText().toString();
            String lTimeStamp = mSimpleDateFormat.format(new Date());
            String lCreateOrEdit = CREATE;


            Intent lIntent = new Intent();
            Bundle lBundleReturn = new Bundle();


            NotesModel lNotesModel;
            if (mFlagEditOrCreate.equals(EDIT)) {
                mCreatedOrModified = MODIFIED;
                lNotesModel = new NotesModel(mID, lTitle, lDes, lTimeStamp, mChordColor, mCreatedOrModified, mFlagSwitchFavorites, mRemainderTime);
                new PerformUpdate().doInBackground(lNotesModel);
                lCreateOrEdit = EDIT;
            } else {
                mCreatedOrModified = CREATED;
                lNotesModel = new NotesModel(mID, lTitle, lDes, lTimeStamp, mChordColor, mCreatedOrModified, mFlagSwitchFavorites, mRemainderTime);
                mID = new PerformInsert().doInBackground(lNotesModel);
            }


            mProgressBar.setVisibility(View.GONE);


            Log.d(this + "", "id==" + mID);


            lBundleReturn.putString(EDIT_OR_CREATE_OR_DELETE, lCreateOrEdit);
            lBundleReturn.putString(DATA_ID, mID + "");
            lBundleReturn.putString(DATA_TITLE, lTitle);
            lBundleReturn.putString(DATA_DES, lDes);
            lBundleReturn.putString(DATA_DATE, lTimeStamp);
            lBundleReturn.putString(CHORD_COLOR, mChordColor);
            lBundleReturn.putString(COLUMN_CREATED_OR_MODIFIED, mCreatedOrModified);
            lBundleReturn.putString(COLUMN_FAVORITE, mFlagSwitchFavorites + "");
            lBundleReturn.putString(COLUMN_REMAINDER_TIME, mRemainderTime + "");
            lIntent.putExtras(lBundleReturn);
            setResult(RESULT_OK, lIntent);
            finish();
        } else {
            super.onBackPressed();
        }

    }


    private void performDelete() {
        final AlertDialog.Builder lBuilder = new AlertDialog.Builder(AddNotesActivity.this);
        lBuilder.setMessage(R.string.delete)
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
        lBuilder.show();
    }


    private boolean validate() {

        return mEtDescription.getText().toString().length() > 0;
    }

    private void showMessage(String pData) {


        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, pData, Toast.LENGTH_SHORT);
        mToast.show();
    }


    class PerformGetNote extends AsyncTask<Long, Void, NotesModel> {
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected NotesModel doInBackground(Long... longs) {
            return mNotesTable.getNote(longs[0]);
        }

        @Override
        protected void onPostExecute(NotesModel notesModel) {
            mProgressBar.setVisibility(View.GONE);
        }
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

        Intent lIntent = new Intent();
        Bundle lBundleReturn = new Bundle();
        lBundleReturn.putString(EDIT_OR_CREATE_OR_DELETE, DELETE);
        lIntent.putExtras(lBundleReturn);
        setResult(RESULT_OK, lIntent);
        finish();
    }
}
