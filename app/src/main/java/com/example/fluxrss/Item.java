package com.example.fluxrss;

import android.graphics.Bitmap;

public class Item {
    private StringBuffer _title = new StringBuffer();
    private StringBuffer _description = new StringBuffer();
    private StringBuffer _date = new StringBuffer();
    private Bitmap _image;

    public StringBuffer getTitle() {
        return _title;
    }

    public StringBuffer getDescription() {
        return _description;
    }

    public StringBuffer getDate() {
        return _date;
    }

    public Bitmap getImage() {
        return _image;
    }

    public void setTitle(StringBuffer title) {
        _title = title;
    }

    public void setDescription(StringBuffer description) {
        _description = description;
    }

    public void setDate(StringBuffer date) {
        _date = date;
    }

    public void setImage(Bitmap image) {
        _image = image;
    }
}
