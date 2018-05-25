package com.mynote.adapter.listner;

import com.mynote.database.model.NotesModel;

public interface INotesRecyclerListner {
    void onClick(NotesModel data,int pos);
}
