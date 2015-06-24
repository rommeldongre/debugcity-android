package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private String pin_code;

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

    public Incident(int _id, double latitude, double longitude, String category, Bitmap image, String pin_code) {
        this._id = _id;
        this.category = category;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pin_code = pin_code;
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

    public String getPin_code() {
        return pin_code;
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

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }

    public void setPin_code(Context context){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pin_code = addressList.get(0).getPostalCode();
    }
}
