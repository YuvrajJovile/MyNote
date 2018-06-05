package com.mynote.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mynote.R;
import com.mynote.adapter.NotesRecyclerAdapter;
import com.mynote.database.NotesTable;
import com.mynote.model.NotesModel;
import com.mynote.utils.NotesApplication;
import com.mynote.utils.RingToneManagerClass;

import java.util.ArrayList;

import static com.mynote.utils.Constants.COLOR_DEFAULT;
import static com.mynote.utils.Constants.COLOR_GREY;
import static com.mynote.utils.Constants.CREATE;
import static com.mynote.utils.Constants.CREATE_CODE;
import static com.mynote.utils.Constants.CURRENT_POS;
import static com.mynote.utils.Constants.EDIT_OR_CREATE_OR_DELETE;
import static com.mynote.utils.Constants.ID_CREATE_OR_EDIT_OR_DELETE;
import static com.mynote.utils.Constants.IN;
import static com.mynote.utils.Constants.LANDSCAPE;
import static com.mynote.utils.Constants.NOTES_LIST;
import static com.mynote.utils.Constants.OUT;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private FloatingActionButton mFabAdd;

    private ArrayList<NotesModel> mNotesListData;


    private RecyclerView mNotesRecycler;
    private NotesRecyclerAdapter mNotesRecyclerAdapter;

    private NotesTable mNotesTable;
    private ProgressBar mProgressBar;
    private int mSelectedPos = -1;

    private TextView mAddNotesText;
    private RelativeLayout mBlinkLayout;
    private ImageView mAddNotesImageView;
    private TextView mtvWelcome;
    private RelativeLayout mBackgroundLay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFabAdd = findViewById(R.id.v_fab_add);
        mNotesRecycler = findViewById(R.id.rv_notes);
        mProgressBar = findViewById(R.id.v_progress);


        mAddNotesText = findViewById(R.id.tv_add_notes);
        mBlinkLayout = findViewById(R.id.v_fab_lay);
        mAddNotesImageView = findViewById(R.id.iv_arrow);
        mtvWelcome = findViewById(R.id.tv_welcome);
        mBackgroundLay = findViewById(R.id.v_background_lay);

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddNotesActivity();
            }
        });


    }


    /**
     * To delete the particular notes data
     *
     * @param pNotesModel
     */
    private void navigateToDelete(final NotesModel pNotesModel) {


        AlertDialog.Builder lAlBuilder = new AlertDialog.Builder(this);

        lAlBuilder.setTitle(R.string.delete);
        lAlBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteNotes().execute(pNotesModel);
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        }).create();
        lAlBuilder.show();

    }


    @Override
    protected void onResume() {

        upDateNotes();

        setWelcomeMessage();

        setOrientation();

        stopAlarmIfRinging();

        super.onResume();

    }

    private void upDateNotes() {

        if (mNotesListData == null)
            mNotesListData = new ArrayList<>();

        mNotesListData.clear();
        mNotesTable = new NotesTable(this);
        new GetNotes().execute();
    }

    private void stopAlarmIfRinging() {
        RingToneManagerClass.getInstance().stopAlarm();
    }


    /**
     * To set welcome message
     */

    private void setWelcomeMessage() {


        String lLoginFlag = NotesApplication.getInstance().getLoginDetails();
        if (lLoginFlag.isEmpty() || lLoginFlag.equals(OUT)
                ) {
            mtvWelcome.setVisibility(View.VISIBLE);
            mtvWelcome.setText(getString(R.string.welcome_message));
            mtvWelcome.setTextColor(Color.parseColor(COLOR_DEFAULT));
            mAddNotesText.setVisibility(View.VISIBLE);
            mAddNotesImageView.setVisibility(View.VISIBLE);
            mBlinkLayout.setBackground(getDrawable(R.drawable.show_case_drawable));
            mBackgroundLay.setBackgroundResource(R.color.colorRedTransparent);
            NotesApplication.getInstance().setLoginFlag(IN);
        } else {

            mAddNotesText.setVisibility(View.GONE);
            mAddNotesImageView.setVisibility(View.GONE);
            mBlinkLayout.setBackground(null);
            mBackgroundLay.setBackgroundResource(0);
            showAddNotesMessage();
        }

    }

    /**
     * To set screen orientation
     */
    private void setOrientation() {

        int orientation = getResources().getConfiguration().orientation;


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        if (orientation == LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, 4);
        }

        mNotesRecycler.setLayoutManager(gridLayoutManager);


        if (mNotesRecyclerAdapter == null) {
            mNotesRecyclerAdapter = new NotesRecyclerAdapter(mNotesListData);
            mNotesRecycler.setAdapter(mNotesRecyclerAdapter);
            NotesRecyclerAdapter.RecyclerTouchListener recyclerTouchListener = new NotesRecyclerAdapter.RecyclerTouchListener(MainActivity.this, mNotesRecycler, new NotesRecyclerAdapter.INotesClickListener() {
                @Override
                public void onClick(View view, int pos) {
                    mSelectedPos = pos;
                    navigateToEdit(mNotesListData.get(pos));
                    Log.d(this + "", "onClick is called");
                }

                @Override
                public void onLongPress(View view, int pos) {
                    mSelectedPos = pos;
                    navigateToDelete(mNotesListData.get(pos));
                    Log.d(this + "", "onLong Press is called");
                }


            });
            mNotesRecycler.addOnItemTouchListener(recyclerTouchListener);
        }

    }


    /**
     * Navigates to Add notes Activity to perform edit
     *
     * @param pData
     */
    private void navigateToEdit(NotesModel pData) {

        Intent lIntent = new Intent(MainActivity.this, ShowNotesActivity.class);
        Bundle lBundle = new Bundle();
        //lBundle.putString(EDIT_OR_CREATE_OR_DELETE, EDIT);
        //lBundle.putParcelable(NOTES_DATA, pData);

        showLog("mSelectedPos==" + mSelectedPos);
        lBundle.putParcelableArrayList(NOTES_LIST, mNotesListData);
        lBundle.putInt(CURRENT_POS, mSelectedPos);
        lIntent.putExtras(lBundle);
        startActivityForResult(lIntent, ID_CREATE_OR_EDIT_OR_DELETE);

    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }


    /**
     * Shows a Toast message
     *
     * @param pData
     */
    private void showMessage(String pData) {
        Toast.makeText(this, pData, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to Add notes Activity to add a new note
     */
    private void navigateToAddNotesActivity() {
        Bundle lBundle = new Bundle();
        Intent lIntent = new Intent(this, AddNotesActivity.class);
        lBundle.putString(EDIT_OR_CREATE_OR_DELETE, CREATE);
        lIntent.putExtras(lBundle);
        startActivityForResult(lIntent, CREATE_CODE);
    }


    /**
     * Handles the result from Add notes activity for instant update
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (data != null) {


            Bundle lResultBundle = data.getExtras();
            NotesModel lNotesModel = null;


            if (lResultBundle != null) {

                lNotesModel = lResultBundle.getParcelable(NOTES_DATA);
            }


            if (resultCode == EDIT_CODE || resultCode == CREATE_CODE) {
                if (resultCode == EDIT_CODE) {

                    for (int i = 0; i < mNotesListData.size(); i++) {

                        if (mNotesListData.get(i).getId() == lNotesModel.getId()) {
                            mNotesListData.remove(i);
                            break;
                        }
                    }

                }


                mNotesListData.add(0, lNotesModel);

                //mNotesRecyclerAdapter.notifyDataSetChanged();
               *//* if (createOrEdit.equals(EDIT)) {

                    // mNotesRecyclerAdapter.notifyItemInserted(0);
                    //mNotesRecyclerAdapter.notifyItemRemoved(mSelectedPos);
                    //  mNotesRecyclerAdapter.notifyItemRangeRemoved(mSelectedPos, mNotesListData.size());
                } else {
                    //  mNotesRecyclerAdapter.notifyItemInserted(0);
                    // mNotesRecyclerAdapter.notifyItemRangeChanged(0,mNotesListData.size());
                    //mNotesRecyclerAdapter.notifyDataSetChanged();
                }*//*


            } else if (resultCode == DELETE_CODE) {
                mNotesListData.remove(mSelectedPos);
                //mNotesRecyclerAdapter.notifyDataSetChanged();
                // mNotesRecyclerAdapter.notifyItemRemoved(mSelectedPos);
                // mNotesRecyclerAdapter.notifyItemRangeChanged(mSelectedPos, mNotesRecyclerAdapter.getItemCount());
            }

            mNotesRecyclerAdapter.notifyDataSetChanged();
            showAddNotesMessage();
        }*/
    }

    /**
     * If the count of the card is zero i.e if there are no notes, "Add Note" Message will be displayed
     */
    private void showAddNotesMessage() {
        if (mNotesListData.size() == 0) {
            mtvWelcome.setVisibility(View.VISIBLE);
            mtvWelcome.setText(getString(R.string.add_notes));
            mtvWelcome.setTextColor(Color.parseColor(COLOR_GREY));
        } else {
            mtvWelcome.setVisibility(View.GONE);
        }
    }

    /**
     * To get all the notes
     */
    private class GetNotes extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            mNotesListData.clear();
            mNotesListData.addAll(mNotesTable.getAllNotes());
            return null;
        }

        @Override
        protected void onPostExecute(Void pVoid) {
            mProgressBar.setVisibility(View.GONE);
            showAddNotesMessage();
        }
    }

    /**
     * To delete particular note
     */
    class DeleteNotes extends AsyncTask<NotesModel, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(NotesModel... pNotesModel) {
            mNotesTable.deleteNotes(pNotesModel[0].getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
            mNotesListData.remove(mSelectedPos);
            mNotesRecyclerAdapter.notifyDataSetChanged();
            showMessage(getString(R.string.note_deleted));

            showAddNotesMessage();

        }
    }


}