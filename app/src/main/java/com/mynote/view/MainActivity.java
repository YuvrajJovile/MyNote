package com.mynote.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.mynote.adapter.listner.INotesRecyclerListner;
import com.mynote.database.NotesTable;
import com.mynote.database.model.NotesModel;
import com.mynote.utils.NotesApplication;

import java.util.ArrayList;
import java.util.List;

import static com.mynote.utils.IConstants.CREATE;
import static com.mynote.utils.IConstants.DATA_DATE;
import static com.mynote.utils.IConstants.DATA_DES;
import static com.mynote.utils.IConstants.DATA_ID;
import static com.mynote.utils.IConstants.DATA_TITLE;
import static com.mynote.utils.IConstants.DELETE;
import static com.mynote.utils.IConstants.EDIT;
import static com.mynote.utils.IConstants.EDIT_OR_CREATE_OR_DELETE;
import static com.mynote.utils.IConstants.ID_CREATE_OR_EDIT_OR_DELETE;
import static com.mynote.utils.IConstants.IN;
import static com.mynote.utils.IConstants.OUT;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mNotesRecycler.setLayoutManager(linearLayoutManager);
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddNotesActivity();
            }
        });
        mNotesListData = new ArrayList<>();


        mNotesTable = new NotesTable(this);

        new GetNotes().doInBackground();

        for (int i = 0; i < mNotesListData.size(); i++) {
            Log.d(TAG, "data ==" + mNotesListData.get(i).getId());
        }


        mProgressBar.setVisibility(View.VISIBLE);
        mNotesRecyclerAdapter = new NotesRecyclerAdapter(mNotesListData, iNotesRecyclerListner);
        mNotesRecycler.setAdapter(mNotesRecyclerAdapter);
        mProgressBar.setVisibility(View.GONE);


        NotesRecyclerAdapter.RecyclerTouchListner recyclerTouchListner = new NotesRecyclerAdapter.RecyclerTouchListner(this, mNotesRecycler, new NotesRecyclerAdapter.NotesClickListner() {
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

        mNotesRecycler.addOnItemTouchListener(recyclerTouchListner);

        // setWelcomeMessage();
        // validateNotesList();
    }

    private void navigateToDelete(final NotesModel pNotesModel) {


        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);

        alBuilder.setTitle(R.string.delete);
        alBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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
        alBuilder.show();

    }

    private void setWelcomeMessage() {
        String flag = NotesApplication.getInstance().getLoginDetails();
        if (flag.isEmpty() || flag.equals(OUT)
                ) {
            mtvWelcome.setVisibility(View.VISIBLE);
            NotesApplication.getInstance().setLoginFlag(IN);
        } else {
            mtvWelcome.setVisibility(View.GONE);
        }

    }

    private void validateNotesList() {
        if (mNotesListData.size() < 1) {

            mAddNotesText.setVisibility(View.VISIBLE);
            mAddNotesImageView.setVisibility(View.VISIBLE);
            mBlinkLayout.setBackground(getDrawable(R.drawable.show_case_drawable));
            mBackgroundLay.setBackgroundResource(R.color.colorPrimaryTransparent);
        } else {
            mAddNotesText.setVisibility(View.GONE);
            mAddNotesImageView.setVisibility(View.GONE);
            mBlinkLayout.setBackground(null);
            mBackgroundLay.setBackgroundResource(0);

        }
    }

    @Override
    protected void onResume() {
        setWelcomeMessage();
        validateNotesList();
        super.onResume();
    }

    private INotesRecyclerListner iNotesRecyclerListner = new INotesRecyclerListner() {
        @Override
        public void onClick(NotesModel data, int pos) {
            //showAllert(data);
        }
    };


    private void navigateToEdit(NotesModel pData) {

        Intent intent = new Intent(MainActivity.this, AddNotesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EDIT_OR_CREATE_OR_DELETE, EDIT);
        bundle.putString(DATA_TITLE, pData.getTitle());
        bundle.putString(DATA_DES, pData.getDescription());
        bundle.putString(DATA_DATE, pData.getDate());
        bundle.putString(DATA_ID, pData.getId() + "");
        intent.putExtras(bundle);
        startActivityForResult(intent, ID_CREATE_OR_EDIT_OR_DELETE);

    }


    private void showMessage(String mData) {
        Toast.makeText(this, mData, Toast.LENGTH_SHORT).show();
    }

    private void navigateToAddNotesActivity() {
        mFlagCreate = true;
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, AddNotesActivity.class);
        bundle.putString(EDIT_OR_CREATE_OR_DELETE, CREATE);
        intent.putExtras(bundle);
        startActivityForResult(intent, ID_CREATE_OR_EDIT_OR_DELETE);
    }


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ID_CREATE_OR_EDIT_OR_DELETE && resultCode == RESULT_OK && data != null && (mSelectedPos != -1 || mFlagCreate)) {

            mFlagCreate = false;

            Bundle resultBundle = data.getExtras();
            NotesModel notesModel = new NotesModel();

            String createOrEdit = resultBundle.getString(EDIT_OR_CREATE_OR_DELETE);

            if (createOrEdit.equals(EDIT) || createOrEdit.equals(CREATE)) {

                long id = Integer.parseInt(resultBundle.getString(DATA_ID));


                Log.d(this + "", "id==" + id);

                notesModel.setId(id);
                notesModel.setTitle(resultBundle.getString(DATA_TITLE));
                notesModel.setDescription(resultBundle.getString(DATA_DES));
                notesModel.setDate(resultBundle.getString(DATA_DATE));

                if (createOrEdit.equals(EDIT) && id != -1) {

                    for (int i = 0; i < mNotesListData.size(); i++) {

                        if (mNotesListData.get(i).getId() == id) {
                            mNotesListData.remove(i);
                            break;
                        }
                    }

                }


                mNotesListData.add(0, notesModel);

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


            } else if (createOrEdit.equals(DELETE)) {
                mNotesListData.remove(mSelectedPos);
                //mNotesRecyclerAdapter.notifyDataSetChanged();
                // mNotesRecyclerAdapter.notifyItemRemoved(mSelectedPos);
                // mNotesRecyclerAdapter.notifyItemRangeChanged(mSelectedPos, mNotesRecyclerAdapter.getItemCount());
            }

            mNotesRecyclerAdapter.notifyDataSetChanged();
            validateNotesList();
        }
    }


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
            validateNotesList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressBar.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
}