package com.mynote.database.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NotesModel implements Parcelable {

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


    public static final Creator<NotesModel> CREATOR = new Creator<NotesModel>() {
        @Override
        public NotesModel createFromParcel(Parcel in) {
            return new NotesModel(in);
        }

        @Override
        public NotesModel[] newArray(int size) {
            return new NotesModel[size];
        }
    };

    protected NotesModel(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        color = in.readString();
        createdOrModified = in.readString();
        favourite = in.readByte() != 0;
        remainderTime = in.readLong();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(color);
        dest.writeString(createdOrModified);
        dest.writeByte((byte) (favourite ? 1 : 0));
        dest.writeLong(remainderTime);
    }
}
