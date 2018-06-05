package com.mynote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mynote.R;
import com.mynote.model.NotesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.mynote.utils.Constants.MODIFIED;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.NotesRecyclerViewHolder> {

    private ArrayList<NotesModel> mDataList;


    public NotesRecyclerAdapter(ArrayList<NotesModel> pDataList) {
        this.mDataList = pDataList;
    }

    @NonNull
    @Override
    public NotesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NotesRecyclerViewHolder lNotesRecyclerViewHolder = new NotesRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_list_item, parent, false));
        lNotesRecyclerViewHolder.setIsRecyclable(false);

        return lNotesRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesRecyclerViewHolder holder, int position) {
        holder.populateData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public interface INotesClickListener {
        void onClick(View view, int pos);

        void onLongPress(View view, int pos);

    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private INotesClickListener mINotesClickListener;
        private GestureDetector mGestureDetector;


        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final INotesClickListener mINotesClickListener) {
            this.mINotesClickListener = mINotesClickListener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View lChild = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (lChild != null && mINotesClickListener != null) {
                        Log.d("RecyclerAdapter", "Long click is called");
                        int pos = recyclerView.getChildAdapterPosition(lChild);
                        mINotesClickListener.onLongPress(lChild, pos);
                    }
                }

            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View lChild = rv.findChildViewUnder(e.getX(), e.getY());
            if (lChild != null && mINotesClickListener != null && mGestureDetector.onTouchEvent(e)) {
                int pos = rv.getChildAdapterPosition(lChild);
                mINotesClickListener.onClick(lChild, pos);
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public class NotesRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private NotesModel mNotesModel;

        private TextView tvTitle, tvDescription;
        private TextView tvDate;
        private CardView mLayout;

        private NotesRecyclerViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate = itemView.findViewById(R.id.tv_date);
            mLayout = itemView.findViewById(R.id.v_layout);
            mLayout.setOnClickListener(this);
        }

        private void populateData(NotesModel notesModel) {
            this.mNotesModel = notesModel;

            SimpleDateFormat lOutputFormat = new SimpleDateFormat("dd MMM yyyy \thh:mm a", Locale.ENGLISH);
            SimpleDateFormat lInputFormat = new SimpleDateFormat("dd:MMM:yyyy hh:mm:ss a", Locale.ENGLISH);


            tvTitle.setText(notesModel.getTitle());


            tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, notesModel.isFavourite() ? R.drawable.ic_favorite_selected :
                    R.drawable.ic_favorite_unselected, 0);


            tvDescription.setText(notesModel.getDescription());

            String lDateString = (notesModel.getCreatedOrModified().equals(MODIFIED)) ? itemView.getContext().getString(R.string.last_modified) :
                    itemView.getContext().getString(R.string.created_at);

            try {
                Date lDate = lInputFormat.parse(notesModel.getDate());
                lDateString += lOutputFormat.format(lDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvDate.setText(lDateString.toUpperCase());

            long lAlarmTime = notesModel.getRemainderTime();
            if (lAlarmTime != -1 && lAlarmTime > Calendar.getInstance().getTimeInMillis())
                tvDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_on, 0, 0, 0);


            try {
                mLayout.setCardBackgroundColor(Color.parseColor(notesModel.getColor()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.v_layout:

                    break;
            }
        }
    }
}

