package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class UserAdd extends ActionBarActivity {

    DBHandler db;
    TextView user_lat_editTextView;
    TextView user_long_editTextView;
    Spinner spinner;
    ImageView user_imageView;
    Bitmap photo;


    public void buttonUserSave(View view){

        new android.app.AlertDialog.Builder(this)
                .setTitle("Save")
                .setMessage("Save this Incident?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Incident incident = new Incident();
                        incident.setLatitude(Double.valueOf(user_lat_editTextView.getText().toString()));
                        incident.setLongitude(Double.valueOf(user_long_editTextView.getText().toString()));
                        incident.setCategory(spinner.getSelectedItem().toString());
                        incident.setImage(photo);
                        db.addIncident(incident, UserAdd.this);
                        Toast.makeText(UserAdd.this, "SAVED", Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_user_add);

        db = new DBHandler(this, null, null, 1);
        user_lat_editTextView = (TextView) findViewById(R.id.user_lat_editTextView);
        user_long_editTextView = (TextView) findViewById(R.id.user_long_editTextView);
        spinner = (Spinner) findViewById(R.id.spinner);
        user_imageView = (ImageView) findViewById(R.id.user_imageView);

        Intent intent = getIntent();
        photo = intent.getParcelableExtra("Photo");
        user_imageView.setImageBitmap(photo);

        //user_imageView.setImageBitmap(photo);
        List<String> categoryList = db.getCategoryList(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        spinner.setAdapter(adapter);

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            user_lat_editTextView.setText(String.valueOf(gps.getLatitude()));
            user_long_editTextView.setText(String.valueOf(gps.getLongitude()));

        }
        else{
            gps.showSettingsAlert(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_add, menu);
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

    //converting bitmap to byte[] and vice-versa:
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getByteArrayAsBitmap(byte[] imgByte){
        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }

    //To show Alert Messages
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
