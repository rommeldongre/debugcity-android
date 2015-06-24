package com.greylabs.sumod.dbct10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class IncidentDetails extends AppCompatActivity {
    DBHandler db;
    ImageView inc_image_view;
    Bitmap bitmap;
    EditText inc_lat_view;
    EditText inc_long_view;
    IncidentList list;
    Spinner spinner_inc_details;
    TextView inc_pincode_view;

    public void buttonEditIncident(View view){
        Button button_edit_incident = (Button) findViewById(R.id.button_edit_incident);

        String text = String.valueOf(button_edit_incident.getText());

        switch (text){
            case "EDIT":
                inc_lat_view.setEnabled(true);
                inc_long_view.setEnabled(true);

                inc_lat_view.setCursorVisible(true);
                inc_long_view.setCursorVisible(true);
                button_edit_incident.setText("SAVE");

                break;

            case "SAVE":
                inc_lat_view.setEnabled(false);
                inc_long_view.setEnabled(false);

                inc_lat_view.setCursorVisible(false);
                inc_long_view.setCursorVisible(false);

                Incident incident = new Incident();

                Intent intent = getIntent();
                int incident_id = intent.getIntExtra("incident_id", 0);

                incident.set_id(incident_id);
                incident.setLatitude(Double.valueOf(String.valueOf(inc_lat_view.getText())));
                incident.setLongitude(Double.valueOf(String.valueOf(inc_long_view.getText())));
                incident.setCategory(spinner_inc_details.getSelectedItem().toString());
                bitmap = ((BitmapDrawable)inc_image_view.getDrawable()).getBitmap();
                incident.setImage(bitmap);
                incident.setPin_code(IncidentDetails.this);
                db.editIncident(incident);
                db.close();
                Toast.makeText(this, "UPDATED", Toast.LENGTH_LONG).show();

                button_edit_incident.setText("EDIT");

                break;
        }
    }

    public void buttonDeleteIncident(View view){

        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = getIntent();
                        int incident_id = intent.getIntExtra("incident_id", 0);
                        db.deleteIncident(incident_id);
                        Toast.makeText(IncidentDetails.this, "DELETED!", Toast.LENGTH_LONG).show();

                        inc_lat_view.setText("");
                        inc_long_view.setText("");
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
        setContentView(R.layout.activity_incident_details);
        db = new DBHandler(this, null, null, 1);

        Intent intent = getIntent();
        int _incident_id = intent.getIntExtra("incident_id", 0);


        Incident incident = new Incident();
        incident = db.getIncident(_incident_id, this);

        inc_lat_view = (EditText) findViewById(R.id.inc_lat_view);
        inc_long_view = (EditText) findViewById(R.id.inc_long_view);
        inc_image_view = (ImageView) findViewById(R.id.inc_image_view);
        spinner_inc_details = (Spinner) findViewById(R.id.spinner_inc_details);
        inc_pincode_view = (TextView) findViewById(R.id.inc_pincode_view);

        inc_lat_view.setEnabled(false);
        inc_long_view.setEnabled(false);

        inc_lat_view.setCursorVisible(false);
        inc_long_view.setCursorVisible(false);

        inc_lat_view.setText(String.valueOf(incident.getLatitude()));
        inc_long_view.setText(String.valueOf(incident.getLongitude()));
        inc_image_view.setImageBitmap(incident.getImage());
        inc_pincode_view.setText(incident.getPin_code());
        List<String> categoryList = db.getCategoryList(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        spinner_inc_details.setAdapter(adapter);

        //ShowAlert("PinCode:", incident.getPin_code());

        //photo = incident.getImage();//this is temporary since we don't yet know how to 'Edit' the image.
        //so to not pass a null object to the setImage() function we're passing what we already have.


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incident_details, menu);
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

}
