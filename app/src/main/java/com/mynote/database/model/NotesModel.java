package com.mynote.database.model;

public class NotesModel {

    private long id;
    private String title;
    private String description;
    private String date;


    public NotesModel() {
    }

    public NotesModel(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public NotesModel(long id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
