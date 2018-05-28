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
import com.mynote.adapter.listner.INotesRecyclerListner;
import com.mynote.database.model.NotesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.mynote.utils.Constants.MODIFIED;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.NotesRecyclerViewHolder> {

    public List<NotesModel> mDataList;
    private INotesRecyclerListner mINotesRecyclerListner;

    public NotesRecyclerAdapter(List<NotesModel> mDataList, INotesRecyclerListner mINotesRecyclerListner) {
        this.mDataList = mDataList;
        this.mINotesRecyclerListner = mINotesRecyclerListner;
    }

    @NonNull
    @Override
    public NotesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NotesRecyclerViewHolder notesRecyclerViewHolder = new NotesRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_list_item, parent, false), mINotesRecyclerListner);
        notesRecyclerViewHolder.setIsRecyclable(false);

        return notesRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesRecyclerViewHolder holder, int position) {
        holder.populateData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class NotesRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private NotesModel mNotesModel;
        private INotesRecyclerListner mINotesRecyclerListner;
        private TextView tvTitle, tvDescription;
        private TextView tvDate;
        private CardView mLayout;

        public NotesRecyclerViewHolder(View itemView, INotesRecyclerListner mINotesRecyclerListner) {
            super(itemView);
            this.mINotesRecyclerListner = mINotesRecyclerListner;
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate = itemView.findViewById(R.id.tv_date);
            mLayout = itemView.findViewById(R.id.v_layout);
            mLayout.setOnClickListener(this);
        }

        public void populateData(NotesModel notesModel) {
            this.mNotesModel = notesModel;

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy \thh:mm a");
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd:MMM:yyyy hh:mm:ss a");


            tvTitle.setText(notesModel.getTitle());


            tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, notesModel.isFavourite() ? R.drawable.ic_favorite_selected :
                    R.drawable.ic_favorite_unselected, 0);


            tvDescription.setText(notesModel.getDescription());

            String dateString = (notesModel.getCreatedOrModified().equals(MODIFIED)) ? itemView.getContext().getString(R.string.last_modified) :
                    itemView.getContext().getString(R.string.created_at);

            try {
                Date date = inputFormat.parse(notesModel.getDate());
                dateString += outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvDate.setText(dateString.toUpperCase());

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
                    mINotesRecyclerListner.onClick(mNotesModel, getAdapterPosition());
                    break;
            }
        }
    }

    public interface NotesClickListner {
        void onClick(View view, int pos);

        void onLongPress(View view, int pos);

    }

    public static class RecyclerTouchListner implements RecyclerView.OnItemTouchListener {

        private NotesClickListner mNotesClickListner;
        private GestureDetector mGestureDetector;


        public RecyclerTouchListner(Context context, final RecyclerView recyclerView, final NotesClickListner mNotesClickListner) {
            this.mNotesClickListner = mNotesClickListner;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mNotesClickListner != null) {
                        Log.d("RecyclerAdapter", "Long click is called");
                        int pos = recyclerView.getChildAdapterPosition(child);
                        mNotesClickListner.onLongPress(child, pos);
                    }
                }

            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && mNotesClickListner != null && mGestureDetector.onTouchEvent(e)) {
                int pos = rv.getChildAdapterPosition(child);
                mNotesClickListner.onClick(child, pos);
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

}

