package com.mynote.database.model;

public class NotesModel {

    private long id;
    private String title;
    private String description;
    private String date;

    private String color;
    private String createdOrModified;

    private boolean favourite;

    private long remainderTime;


    public NotesModel() {
    }

    public NotesModel(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public NotesModel(long id, String title, String description,
                      String date, String color, String createdOrModified
            , boolean favorite, long remainderTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.color = color;
        this.createdOrModified = createdOrModified;
        this.favourite = favorite;
        this.remainderTime = remainderTime;
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


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCreatedOrModified() {
        return createdOrModified;
    }

    public void setCreatedOrModified(String createdOrModified) {
        this.createdOrModified = createdOrModified;
    }


    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public long getRemainderTime() {
        return remainderTime;
    }

    public void setRemainderTime(long remainderTime) {
        this.remainderTime = remainderTime;
    }
}
