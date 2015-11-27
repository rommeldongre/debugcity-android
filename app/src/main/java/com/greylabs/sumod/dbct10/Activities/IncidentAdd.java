package com.greylabs.sumod.dbct10.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.GPSTracker;
import com.greylabs.sumod.dbct10.Model.Incident;
import com.greylabs.sumod.dbct10.R;

import java.util.List;


public class IncidentAdd extends AppCompatActivity {

    DBHandler db;
    Button button_save_incident;
    EditText inc_lat_editTextView;
    EditText inc_long_editTextView;
    Spinner spinner_category;
    IncidentList list = new IncidentList();
    ImageView inc_imageView;
    Bitmap photo;
    Bitmap bitmap;
    double latitude;
    double longitude;

    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public void buttonShootIncident(View view){
        if(!hasCamera())
            Toast.makeText(this, "No camera on device", Toast.LENGTH_SHORT).show();
        else{
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void buttonSelectImage(View view){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void buttonGetLocation(View view){
        inc_lat_editTextView.setEnabled(true);
        inc_long_editTextView.setEnabled(true);

        GPSTracker gps = new GPSTracker(this);

        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            inc_lat_editTextView.setText(String.valueOf(gps.getLatitude()));
            inc_long_editTextView.setText(String.valueOf(gps.getLongitude()));
            Incident incident = new Incident(latitude, longitude);
            incident.setPin_code(this);
            Toast.makeText(this, "PinCode: " + incident.getPin_code(), Toast.LENGTH_LONG).show();
        }
        else {
            gps.showSettingsAlert(this);
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
                        incident.setCategory(spinner_category.getSelectedItem().toString());
                        incident.setPin_code(IncidentAdd.this);
                        //bitmap = ((BitmapDrawable)inc_imageView.getDrawable()).getBitmap();
                        //incident.setImage(bitmap);
                        db.addIncident(incident, IncidentAdd.this);
                        Toast.makeText(IncidentAdd.this, "SAVED", Toast.LENGTH_SHORT).show();
                        inc_lat_editTextView.setText("");
                        inc_long_editTextView.setText("");

                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.stat_sys_warning)
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
        spinner_category = (Spinner) findViewById(R.id.spinner_category);
        inc_imageView = (ImageView) findViewById(R.id.inc_imageView);

        List<String> categoryList = db.getCategoryList(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        spinner_category.setAdapter(adapter);

        /*
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
        */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            inc_imageView.setImageBitmap(photo);
        }
        else if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            inc_imageView.setVisibility(View.VISIBLE);
            inc_imageView.setImageURI(selectedImageUri);
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

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
