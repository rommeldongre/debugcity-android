package com.greylabs.sumod.dbct10;

import android.graphics.Bitmap;

/**
 * Created by Sumod on 6/3/2015.
 */
public class Incident {

    //private variables:
    private int _id;
    private double latitude;
    private double longitude;
    private String category;
    private Bitmap image;

    //Constructors:
    public Incident(int _id, double latitude, double longitude, String category){
        this._id = _id;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Incident(int _id, double latitude, double longitude, String category, Bitmap image){
        this._id = _id;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public Incident(double latitude, double longitude, String category) {
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Incident(){

    }

    //Getters:

    public int get_id() {
        return _id;
    }

    public String getCategory() {
        return category;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Bitmap getImage() {
        return image;
    }

    //Setters:

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
