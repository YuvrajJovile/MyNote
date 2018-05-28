package com.mynote.database.model;

public class NotesModel {

    private long id;
    private String title;
    private String description;
    private String date;

    private String color;
    private String createdOrModified;


    public NotesModel() {
    }

    public NotesModel(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public NotesModel(long id, String title, String description, String date, String color, String createdOrModified) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.color = color;
        this.createdOrModified = createdOrModified;
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
}
