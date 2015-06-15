package com.greylabs.sumod.dbct10;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;


public class IncidentAdd extends AppCompatActivity {
    DBHandler db;
    Button button_save_incident;
    EditText inc_lat_editTextView;
    EditText inc_long_editTextView;
    EditText inc_cat_editTextView;
    IncidentList list = new IncidentList();
    ImageView inc_imageView;
    Bitmap photo;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public void buttonShootIncident(View view){
        if(!hasCamera())
            Toast.makeText(this, "No camera on device", Toast.LENGTH_SHORT).show();
        else{
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void buttonSaveIncident(View view){
        

        new android.app.AlertDialog.Builder(this)
                .setTitle("Save")
                .setMessage("Save this Incident?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Incident incident = new Incident();
                        incident.setLatitude(Double.valueOf(inc_lat_editTextView.getText().toString()));
                        incident.setLongitude(Double.valueOf(inc_long_editTextView.getText().toString()));
                        incident.setCategory(inc_cat_editTextView.getText().toString());
                        incident.setImage(photo);
                        db.addIncident(incident, IncidentAdd.this);
                        Toast.makeText(IncidentAdd.this, "SAVED", Toast.LENGTH_SHORT).show();
                        inc_lat_editTextView.setText("");
                        inc_long_editTextView.setText("");
                        inc_cat_editTextView.setText("");

                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_add);
        //ShowAlert("Pop up", "OnCreate IncidentAdd");
        db = new DBHandler(this, null, null, 1);
        button_save_incident = (Button) findViewById(R.id.button_save_incident);
        inc_lat_editTextView = (EditText) findViewById(R.id.inc_lat_editTextView);
        inc_long_editTextView = (EditText) findViewById(R.id.inc_long_editTextView);
        inc_cat_editTextView = (EditText) findViewById(R.id.inc_cat_editTextView);
        inc_imageView = (ImageView) findViewById(R.id.inc_imageView);

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            inc_lat_editTextView.setText(String.valueOf(gps.getLatitude()));
            inc_long_editTextView.setText(String.valueOf(gps.getLongitude()));

            inc_lat_editTextView.setEnabled(false);
            inc_long_editTextView.setEnabled(false);
        }
        else{
            gps.showSettingsAlert(this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            inc_imageView.setImageBitmap(photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incident_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void ShowAlert(String title, String message){
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
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

    public void buttonGetLocation(View view){
        inc_lat_editTextView.setEnabled(true);
        inc_long_editTextView.setEnabled(true);

        GPSTracker gps = new GPSTracker(this);

        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            inc_lat_editTextView.setText(String.valueOf(gps.getLatitude()));
            inc_long_editTextView.setText(String.valueOf(gps.getLongitude()));
            Toast.makeText(this, "Lat: " + String.valueOf(latitude) + "\nLong: " + String.valueOf(longitude), Toast.LENGTH_LONG).show();
        }
        else {
            gps.showSettingsAlert(this);
        }
    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}
