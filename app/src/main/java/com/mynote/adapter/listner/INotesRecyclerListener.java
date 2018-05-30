package com.mynote.adapter.listner;

import com.mynote.database.model.NotesModel;

public interface INotesRecyclerListener {
    void onClick(NotesModel data,int pos);
}
