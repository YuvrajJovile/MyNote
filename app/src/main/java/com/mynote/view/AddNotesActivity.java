package com.mynote.view;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.mynote.model.NotesModel;
import com.mynote.utils.RingToneManagerClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.mynote.utils.Constants.COLOR_BLUE;
import static com.mynote.utils.Constants.COLOR_DEFAULT;
import static com.mynote.utils.Constants.COLOR_GREEN;
import static com.mynote.utils.Constants.COLOR_GREEN_LIGHT;
import static com.mynote.utils.Constants.COLOR_ORANGE;
import static com.mynote.utils.Constants.COLOR_PINK;
import static com.mynote.utils.Constants.COLOR_VIOLET;
import static com.mynote.utils.Constants.CREATED;
import static com.mynote.utils.Constants.CREATE_CODE;
import static com.mynote.utils.Constants.DELETE_CODE;
import static com.mynote.utils.Constants.EDIT;
import static com.mynote.utils.Constants.EDIT_CODE;
import static com.mynote.utils.Constants.EDIT_OR_CREATE_OR_DELETE;
import static com.mynote.utils.Constants.MODIFIED;
import static com.mynote.utils.Constants.NOTES_DATA;

public class AddNotesActivity extends AppCompatActivity {

    private EditText mEtTitle, mEtDescription;

    private final String TAG = getClass().getSimpleName();

    private ImageButton mIbDelete, mIbFavorites, mIbRemainder, mIbShare;

    private TextView mtvDateModified, mtvRemainder, mTvCardColor;
    private Toast mToast;
    private long mID = -1;
    private ProgressBar mProgressBar;
    private ImageButton mIbBack;

    private RadioGroup mRgColorGroup;

    private String mFlagEditOrCreate = null;

    private NotesTable mNotesTable;

    private boolean mRemainderSet = false;

    private boolean mFlagChangesMade = false;

    private Calendar mAlarmCalender;

    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;


    private NotesModel mNotesModel = null;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd:MMM:yyyy hh:mm:ss a", Locale.ENGLISH);


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
        mTvCardColor = findViewById(R.id.tv_card_color);


        init();

    }

    private void init() {

        mAlarmCalender = Calendar.getInstance();

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


            if (mFlagEditOrCreate != null && mFlagEditOrCreate.equals(EDIT)) {


                mIbDelete.setVisibility(View.VISIBLE);

                mNotesModel = lBundle.getParcelable(NOTES_DATA);

                mID = mNotesModel.getId();

                String lTitleString = mNotesModel.getTitle();
                if (lTitleString == null || lTitleString.length() == 0)
                    lTitleString = "No Title";
                mEtTitle.setText(lTitleString);
                mEtTitle.setSelection(lTitleString.length());

                mEtDescription.setText(mNotesModel.getDescription());


                long lRemainderTime = mNotesModel.getRemainderTime();

                if (lRemainderTime < mAlarmCalender.getTimeInMillis()) {
                    mNotesModel.setRemainderTime(-1);
                    mRemainderSet = false;
                    mIbRemainder.setSelected(false);

                } else {
                    mRemainderSet = true;
                    mIbRemainder.setSelected(true);
                    mtvRemainder.setVisibility(View.VISIBLE);
                    mAlarmCalender.setTimeInMillis(lRemainderTime);

                    int hour = mAlarmCalender.get(Calendar.HOUR);
                    hour = (hour == 0) ? 12 : hour;
                    String lRemainderText = "Remainder set on: " + mAlarmCalender.get(Calendar.DAY_OF_MONTH) + "/" + (mAlarmCalender.get(Calendar.MONTH) + 1)
                            + "/" + mAlarmCalender.get(Calendar.YEAR) + "\t" + hour + ":" + mAlarmCalender.get(Calendar.MINUTE);
                    mtvRemainder.setText(lRemainderText);

                }

                if (mNotesModel.isFavourite()) {
                    mIbFavorites.setSelected(true);
                } else {
                    mIbFavorites.setSelected(false);
                }


                int id = -1;

                String lCardColor = mNotesModel.getColor();

                mTvCardColor.setTextColor(Color.parseColor(lCardColor));

                if (lCardColor.equals(COLOR_DEFAULT)) {
                    id = 0;
                } else if (lCardColor.equals(COLOR_GREEN)) {
                    id = R.id.rb_green;
                } else if (lCardColor.equals(COLOR_GREEN_LIGHT)) {
                    id = R.id.rb_green_light;
                } else if (lCardColor.equals(COLOR_BLUE)) {
                    id = R.id.rb_color_blue;
                } else if (lCardColor.equals(COLOR_VIOLET)) {
                    id = R.id.rb_color_violet;
                } else if (lCardColor.equals(COLOR_PINK)) {
                    id = R.id.rb_color_pink;
                } else if (lCardColor.equals(COLOR_ORANGE)) {
                    id = R.id.rb_color_orange;
                }


                if (id != 0)
                    mRgColorGroup.check(id);


                String lDateString = mNotesModel.getDate();
                SimpleDateFormat lOutputFormat;
                lOutputFormat = new SimpleDateFormat("dd MMM \thh:mm a", Locale.ENGLISH);

                try {
                    Date lDate = mSimpleDateFormat.parse(lDateString);
                    lDateString = lOutputFormat.format(lDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String lModifiedTitle = ((mNotesModel.getCreatedOrModified().equals(MODIFIED)) ? getString(R.string.last_modified) : getString(R.string.created_at)) + lDateString;
                mtvDateModified.setText(lModifiedTitle);

                Log.d(this + "", "id==" + mID);
            } else {
                mNotesModel = new NotesModel();
                mNotesModel.setRemainderTime(-1);
                mNotesModel.setColor(COLOR_DEFAULT);
                mNotesModel.setCreatedOrModified(CREATED);
                mNotesModel.setFavourite(false);
            }


            if (mNotesModel.getRemainderTime() != -1) {
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
                    ShowReminderEditDialog();
                } else if (validate())
                    performOnClickRemainder();

            }
        });

        mRgColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                mFlagChangesMade = true;
                String lColor = COLOR_DEFAULT;

                switch (checkedId) {
                    case R.id.rb_green:

                        lColor = COLOR_GREEN;
                        break;
                    case R.id.rb_green_light:

                        lColor = COLOR_GREEN_LIGHT;
                        break;
                    case R.id.rb_color_blue:

                        lColor = COLOR_BLUE;
                        break;
                    case R.id.rb_color_violet:

                        lColor = COLOR_VIOLET;
                        break;
                    case R.id.rb_color_pink:

                        lColor = COLOR_PINK;
                        break;
                    case R.id.rb_color_orange:

                        lColor = COLOR_ORANGE;
                        break;
                }

                mNotesModel.setColor(lColor);
                mTvCardColor.setTextColor(Color.parseColor(lColor));

            }
        });


        mEtTitle.addTextChangedListener(new TextWatcher() {
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

    private void showLog(String pMessage) {

        Log.d(TAG, pMessage);
    }


    /**
     * To edit the remainder set before
     */
    private void ShowReminderEditDialog() {

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

    /**
     * To delete the remainder set before
     */
    private void cancelRemainder() {
        RingToneManagerClass.getInstance().stopAlarm();

        if (mPendingIntent != null) {
            mAlarmManager.cancel(mPendingIntent);
            mtvRemainder.setVisibility(View.GONE);

            mIbRemainder.setSelected(false);
            mRemainderSet = false;

            mNotesModel.setRemainderTime(-1);
            mFlagChangesMade = true;


            showMessage(getString(R.string.remainder_canceled));

        }
    }

    @Override
    protected void onResume() {
        //setOrientation();
        super.onResume();
    }
/*
    private void setOrientation() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == POTRAIT) {

        }
        else if (orientation == LANDSCAPE) {
        }
    }*/


    /**
     * To set Remainder
     */
    private void performOnClickRemainder() {


        TimePickerDialog.OnTimeSetListener lOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                mAlarmCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mAlarmCalender.set(Calendar.MINUTE, minute);
                setOrEditAlarm(mAlarmCalender);
            }
        };
        final TimePickerDialog lTimePickerDialog = new TimePickerDialog(this, lOnTimeSetListener, mAlarmCalender.get(Calendar.HOUR_OF_DAY), mAlarmCalender.get(Calendar.MINUTE), false);


        DatePickerDialog.OnDateSetListener lOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                mAlarmCalender.set(Calendar.YEAR, year);
                mAlarmCalender.set(Calendar.MONTH, month);
                mAlarmCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                lTimePickerDialog.show();
            }
        };


        DatePickerDialog lDatePickerDialog = new DatePickerDialog(this, lOnDateSetListener, mAlarmCalender.get(Calendar.YEAR), mAlarmCalender.get(Calendar.MONTH), mAlarmCalender.get(Calendar.DAY_OF_MONTH));
        lDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        lDatePickerDialog.show();


    }


    /**
     * Setting and editing the remainder
     *
     * @param pAlarmCalendar
     */
    private void setOrEditAlarm(Calendar pAlarmCalendar) {

        mFlagChangesMade = true;
        String lTitle = mEtTitle.getText().toString().isEmpty() ? getString(R.string.no_title) : mEtTitle.getText().toString();


        if (mFlagEditOrCreate.equals(EDIT))
            mNotesModel.setCreatedOrModified(MODIFIED);


        mNotesModel.setRemainderTime(pAlarmCalendar.getTimeInMillis());
        new PerformUpdate().execute(mNotesModel);
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
        lIntent.putExtra("message", lTitle);
        mPendingIntent = PendingIntent.getBroadcast(this, (int) mID, lIntent, 0);
        mAlarmManager.set(AlarmManager.RTC, pAlarmCalendar.getTimeInMillis(), mPendingIntent);
        mRemainderSet = true;
        showMessage(getString(R.string.remainder_set));
        mIbRemainder.setSelected(true);
    }


    /**
     * Handles the favorites
     */
    private void performOnCLickFavorites() {

        mFlagChangesMade = true;
        if (mIbFavorites.isSelected()) {
            mIbFavorites.setSelected(false);
        } else {
            mIbFavorites.setSelected(true);
        }


        mNotesModel.setFavourite(mIbFavorites.isSelected());


    }


    /**
     * To perform share
     */
    private void performShare() {

        String lTitle = mEtTitle.getText().toString();
        lTitle = lTitle.isEmpty() ? getString(R.string.no_title) : lTitle;
        String lDataToShare = getString(R.string.share_title) + lTitle + getString(R.string.share_description) + mEtDescription.getText().toString();
        Intent lSendIntent = new Intent();
        lSendIntent.setAction(Intent.ACTION_SEND);
        lSendIntent.putExtra(Intent.EXTRA_TEXT, lDataToShare);
        lSendIntent.setType("text/plain");
        startActivity(lSendIntent);

    }


    /**
     * Auto save on - ON BACK PRESSED, if no changes are made activity is finished
     */
    @Override
    public void onBackPressed() {

        if (validate() && mFlagChangesMade) {

            mProgressBar.setVisibility(View.VISIBLE);


            String lTitle = mEtTitle.getText().toString();
            lTitle = lTitle.isEmpty() || lTitle.equals("") ? getString(R.string.no_title) : lTitle;
            String lDes = mEtDescription.getText().toString();
            String lTimeStamp = mSimpleDateFormat.format(new Date());


            Intent lIntent = new Intent();
            Bundle lBundleReturn = new Bundle();

            if (mNotesModel == null)
                mNotesModel = new NotesModel();

            mNotesModel.setId(mID);
            mNotesModel.setTitle(lTitle);
            mNotesModel.setDescription(lDes);
            mNotesModel.setDate(lTimeStamp);


            int lResultCode = CREATE_CODE;

            if (mFlagEditOrCreate.equals(EDIT)) {
                lResultCode = EDIT_CODE;
                mNotesModel.setCreatedOrModified(MODIFIED);
                new PerformUpdate().execute(mNotesModel);

            } else {

                mNotesModel.setCreatedOrModified(CREATED);
                new PerformInsert().execute(mNotesModel);
            }


            mProgressBar.setVisibility(View.GONE);


            Log.d(this + "", "id==" + mID);


            lBundleReturn.putParcelable(NOTES_DATA, mNotesModel);

            lIntent.putExtras(lBundleReturn);
            setResult(lResultCode, lIntent);
            finish();
        } else {
            super.onBackPressed();
        }

    }


    /**
     * To confirm the note to be deleted
     */
    private void performDelete() {
        final AlertDialog.Builder lBuilder = new AlertDialog.Builder(AddNotesActivity.this);
        lBuilder.setMessage(R.string.delete)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PerformDelete().execute();
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


    /**
     * validates for empty description
     *
     * @return
     */
    private boolean validate() {


        String lDes = mEtDescription.getText().toString();

        //check for white space

        String lTemp = lDes.replaceAll(" ", "");
        if (lTemp.length() == 0) {
            return false;
        }

        return lDes.length() > 0;
    }


    /**
     * To show a Toast message
     *
     * @param pData
     */
    private void showMessage(String pData) {


        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, pData, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * Navigates to Main Activity
     */
    private void navigateToMain() {

        Intent lIntent = new Intent();
        setResult(DELETE_CODE, lIntent);
        finish();
    }

    /**
     * To perform delete
     */
    @SuppressLint("StaticFieldLeak")
    class PerformDelete extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mID != -1) {
                mNotesTable.deleteNotes(mID);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
            navigateToMain();
        }
    }

    /**
     * To perform update
     */

    @SuppressLint("StaticFieldLeak")
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

    /**
     * To Perform Insert
     */
    @SuppressLint("StaticFieldLeak")
    class PerformInsert extends AsyncTask<NotesModel, Void, Void> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(NotesModel... notesModels) {
            mID = mNotesTable.insertNote(notesModels[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aLong) {
            mProgressBar.setVisibility(View.GONE);

        }

    }
}
