package com.mynote.view;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mynote.R;
import com.mynote.broadcast.RemainderBroadcast;
import com.mynote.model.NotesModel;
import com.mynote.utils.RingToneManagerClass;

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
import static com.mynote.utils.Constants.NOTES_DATA;

public class AddNotesActivity extends AppCompatActivity {

    private EditText mEtTitle, mEtDescription;

    private final String TAG = getClass().getSimpleName();

    private ImageButton mIbFavorites;
    private ImageButton mIbRemainder;

    private TextView mtvRemainder, mTvCardColor;

    private Toast mToast;


    private boolean mRemainderSet = false;

    private boolean mFlagChangesMade = false;

    private Calendar mAlarmCalender;

    private PendingIntent mPendingIntent;

    private AlarmManager mAlarmManager;

    private NotesModel mNotesModel = new NotesModel();

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd:MMM:yyyy hh:mm:ss a", Locale.ENGLISH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        mToast = new Toast(this);
        mEtTitle = findViewById(R.id.et_title);
        mEtDescription = findViewById(R.id.et_description);

        mtvRemainder = findViewById(R.id.tv_remainder);

        mIbFavorites = findViewById(R.id.ib_favourites);
        mIbRemainder = findViewById(R.id.ib_remainder);
        mTvCardColor = findViewById(R.id.tv_card_color);

        ImageButton lIbShare = findViewById(R.id.ib_share);
        ImageButton lIbBack = findViewById(R.id.ib_back);
        RadioGroup lRgColorGroup = findViewById(R.id.rg_color_group);


        mEtTitle.setCursorVisible(false);
        mEtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtTitle.setCursorVisible(true);
            }
        });


        mAlarmCalender = Calendar.getInstance();

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        lIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lIbShare.setOnClickListener(new View.OnClickListener()

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
                } else if (validate()) {
                    performOnClickRemainder();
                } else {
                    showMessage("Enter a note");
                }
            }
        });

        lRgColorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

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


        mNotesModel.setColor(COLOR_DEFAULT);
        mNotesModel.setRemainderTime(-1);
        mNotesModel.setCreatedOrModified(CREATED);
    }


    /**
     * Show log.d
     *
     * @param pMessage custom message
     */
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
            mtvRemainder.setVisibility(View.INVISIBLE);

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


        mNotesModel.setRemainderTime(pAlarmCalendar.getTimeInMillis());

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

        long mId = Calendar.getInstance().getTimeInMillis();

        lIntent.putExtra("id", mId);
        lIntent.putExtra("message", lTitle);
        mPendingIntent = PendingIntent.getBroadcast(this, (int) mId, lIntent, 0);
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

        showLog("OnBackPressed");

        if (validate() && mFlagChangesMade) {


            String lTitle = mEtTitle.getText().toString();
            lTitle = lTitle.isEmpty() || lTitle.equals("") ? getString(R.string.no_title) : lTitle;
            String lDes = mEtDescription.getText().toString();
            String lTimeStamp = mSimpleDateFormat.format(new Date());


            Intent lIntent = new Intent();
            Bundle lBundleReturn = new Bundle();


            mNotesModel.setFavourite(mIbFavorites.isSelected());
            mNotesModel.setTitle(lTitle);
            mNotesModel.setDescription(lDes);
            mNotesModel.setDate(lTimeStamp);


            int lResultCode = CREATE_CODE;


            lBundleReturn.putParcelable(NOTES_DATA, mNotesModel);

            lIntent.putExtras(lBundleReturn);
            setResult(lResultCode, lIntent);
            finish();
        } else {
            super.onBackPressed();
        }

    }


    private boolean validate() {

        String lDes = mEtDescription.getText().toString();

        return lDes.replaceAll("", "").length() > 0;
    }


    /**
     * To show a Toast message
     *
     * @param pData custom message
     */
    private void showMessage(String pData) {


        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, pData, Toast.LENGTH_SHORT);
        mToast.show();
    }


}
