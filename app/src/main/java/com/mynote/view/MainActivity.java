package com.mynote.view;

import android.annotation.SuppressLint;
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
import com.mynote.adapter.listner.INotesRecyclerListener;
import com.mynote.database.NotesTable;
import com.mynote.database.model.NotesModel;
import com.mynote.utils.NotesApplication;

import java.util.ArrayList;
import java.util.List;

import static com.mynote.utils.Constants.CHORD_COLOR;
import static com.mynote.utils.Constants.COLOR_GREY;
import static com.mynote.utils.Constants.COLUMN_CREATED_OR_MODIFIED;
import static com.mynote.utils.Constants.COLUMN_FAVORITE;
import static com.mynote.utils.Constants.COLUMN_REMAINDER_TIME;
import static com.mynote.utils.Constants.CREATE;
import static com.mynote.utils.Constants.DATA_DATE;
import static com.mynote.utils.Constants.DATA_DES;
import static com.mynote.utils.Constants.DATA_ID;
import static com.mynote.utils.Constants.DATA_TITLE;
import static com.mynote.utils.Constants.DELETE;
import static com.mynote.utils.Constants.EDIT;
import static com.mynote.utils.Constants.EDIT_OR_CREATE_OR_DELETE;
import static com.mynote.utils.Constants.ID_CREATE_OR_EDIT_OR_DELETE;
import static com.mynote.utils.Constants.IN;
import static com.mynote.utils.Constants.LANDSCAPE;
import static com.mynote.utils.Constants.OUT;
import static com.mynote.utils.Constants.POTRAIT;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private FloatingActionButton mFabAdd;

    private List<NotesModel> mNotesListData;


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
    private boolean mFlagCreate = false;


    private INotesRecyclerListener iNotesRecyclerListener = new INotesRecyclerListener() {
        @Override
        public void onClick(NotesModel data, int pos) {
            //showAllert(data);
        }
    };

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


        init();

    }

    private void init() {
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddNotesActivity();
            }
        });
        mNotesListData = new ArrayList<>();
        mNotesTable = new NotesTable(this);
        new GetNotes().doInBackground();
    }

    private void navigateToDelete(final NotesModel pNotesModel) {


        final AlertDialog.Builder lAlBuilder = new AlertDialog.Builder(this);

        lAlBuilder.setTitle(R.string.delete);
        lAlBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteNotes().doInBackground(pNotesModel);
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

        Log.d(TAG, "Orientation Changed");
        setWelcomeMessage();

        setOrientation();

        super.onResume();
    }

    private void setWelcomeMessage() {
        String lLoginFlag = NotesApplication.getInstance().getLoginDetails();
        if (lLoginFlag.isEmpty() || lLoginFlag.equals(OUT)
                ) {
            mtvWelcome.setVisibility(View.VISIBLE);
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


            if (mNotesListData.size() < 1) {
                mtvWelcome.setVisibility(View.VISIBLE);
                mtvWelcome.setText(getString(R.string.add_notes));
                mtvWelcome.setTextColor(Color.parseColor(COLOR_GREY));
            } else {
                mtvWelcome.setVisibility(View.GONE);
            }
        }

    }

    private void setOrientation() {

        int orientation = getResources().getConfiguration().orientation;


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        if (orientation == POTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else if (orientation == LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, 4);
        }

        mNotesRecycler.setLayoutManager(gridLayoutManager);


        if (mNotesRecyclerAdapter == null) {
            mNotesRecyclerAdapter = new NotesRecyclerAdapter(mNotesListData, iNotesRecyclerListener);
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

    private void navigateToEdit(NotesModel pData) {

        Intent lIntent = new Intent(MainActivity.this, AddNotesActivity.class);
        Bundle lBundle = new Bundle();
        lBundle.putString(EDIT_OR_CREATE_OR_DELETE, EDIT);
        lBundle.putString(DATA_TITLE, pData.getTitle());
        lBundle.putString(DATA_DES, pData.getDescription());
        lBundle.putString(DATA_DATE, pData.getDate());
        lBundle.putString(DATA_ID, pData.getId() + "");
        lBundle.putString(CHORD_COLOR, pData.getColor());
        lBundle.putString(COLUMN_CREATED_OR_MODIFIED, pData.getCreatedOrModified());
        lBundle.putString(COLUMN_FAVORITE, pData.isFavourite() + "");
        lBundle.putString(COLUMN_REMAINDER_TIME, pData.getRemainderTime() + "");
        lIntent.putExtras(lBundle);
        startActivityForResult(lIntent, ID_CREATE_OR_EDIT_OR_DELETE);

    }


    private void showMessage(String pData) {
        Toast.makeText(this, pData, Toast.LENGTH_SHORT).show();
    }

    private void navigateToAddNotesActivity() {
        mFlagCreate = true;
        Bundle lBundle = new Bundle();
        Intent lIntent = new Intent(this, AddNotesActivity.class);
        lBundle.putString(EDIT_OR_CREATE_OR_DELETE, CREATE);
        lIntent.putExtras(lBundle);
        startActivityForResult(lIntent, ID_CREATE_OR_EDIT_OR_DELETE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ID_CREATE_OR_EDIT_OR_DELETE && resultCode == RESULT_OK && data != null && (mSelectedPos != -1 || mFlagCreate)) {

            mFlagCreate = false;

            Bundle lResultBundle = data.getExtras();
            NotesModel lNotesModel = new NotesModel();


            String lCreateOrEdit = null;

            if (lResultBundle != null)
                lCreateOrEdit = lResultBundle.getString(EDIT_OR_CREATE_OR_DELETE);

            if (lCreateOrEdit != null) {
                if (lCreateOrEdit.equals(EDIT) || lCreateOrEdit.equals(CREATE)) {

                    long id = Integer.parseInt(lResultBundle.getString(DATA_ID));


                    Log.d(this + "", "id==" + id);

                    lNotesModel.setId(id);
                    lNotesModel.setTitle(lResultBundle.getString(DATA_TITLE));
                    lNotesModel.setDescription(lResultBundle.getString(DATA_DES));
                    lNotesModel.setDate(lResultBundle.getString(DATA_DATE));
                    lNotesModel.setColor(lResultBundle.getString(CHORD_COLOR));
                    lNotesModel.setCreatedOrModified(lResultBundle.getString(COLUMN_CREATED_OR_MODIFIED));
                    lNotesModel.setFavourite(Boolean.parseBoolean(lResultBundle.getString(COLUMN_FAVORITE)));
                    lNotesModel.setRemainderTime(Long.parseLong(lResultBundle.getString(COLUMN_REMAINDER_TIME)));

                    if (lCreateOrEdit.equals(EDIT) && id != -1) {

                        for (int i = 0; i < mNotesListData.size(); i++) {

                            if (mNotesListData.get(i).getId() == id) {
                                mNotesListData.remove(i);
                                break;
                            }
                        }

                    }


                    mNotesListData.add(0, lNotesModel);

                    //mNotesRecyclerAdapter.notifyDataSetChanged();
               /* if (createOrEdit.equals(EDIT)) {

                    // mNotesRecyclerAdapter.notifyItemInserted(0);
                    //mNotesRecyclerAdapter.notifyItemRemoved(mSelectedPos);
                    //  mNotesRecyclerAdapter.notifyItemRangeRemoved(mSelectedPos, mNotesListData.size());
                } else {
                    //  mNotesRecyclerAdapter.notifyItemInserted(0);
                    // mNotesRecyclerAdapter.notifyItemRangeChanged(0,mNotesListData.size());
                    //mNotesRecyclerAdapter.notifyDataSetChanged();
                }*/


                } else if (lCreateOrEdit.equals(DELETE)) {
                    mNotesListData.remove(mSelectedPos);
                    //mNotesRecyclerAdapter.notifyDataSetChanged();
                    // mNotesRecyclerAdapter.notifyItemRemoved(mSelectedPos);
                    // mNotesRecyclerAdapter.notifyItemRangeChanged(mSelectedPos, mNotesRecyclerAdapter.getItemCount());
                }
            }

            mNotesRecyclerAdapter.notifyDataSetChanged();
            setWelcomeMessage();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetNotes extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
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
            super.onPostExecute(pVoid);

        }
    }

    @SuppressLint("StaticFieldLeak")
    class DeleteNotes extends AsyncTask<NotesModel, Void, Void> {


        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(NotesModel... pNotesModel) {
            mNotesTable.deleteNotes(pNotesModel[0].getId());
            mNotesListData.remove(mSelectedPos);
            mNotesRecyclerAdapter.notifyDataSetChanged();
            showMessage(getString(R.string.note_deleted));
            setWelcomeMessage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
}