package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

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
    private String pin_code = "Unknown";

    //Constructors:


    public Incident(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

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
        if(pin_code == null)
            pin_code = "Unknown";
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
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList == null || addressList.size() == 0) {
                pin_code = "Unknown";
            } else {
                pin_code = addressList.get(0).getPostalCode();
                //ShowAlert("Address: ", addressList.get(i).toString());
            }
        } catch (IOException e) {
            //ShowAlert("Pincode exception: ", e.getMessage());
            e.printStackTrace();
        }

    }

    public void ShowAlert(String title, String message, Context context) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });
        alertDialog.setIcon(R.drawable.abc_dialog_material_background_dark);
        alertDialog.show();
    }
}
