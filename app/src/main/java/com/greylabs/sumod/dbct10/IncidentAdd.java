package com.greylabs.sumod.dbct10;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;


public class IncidentAdd extends ActionBarActivity {
    DBHandler db;
    Button button_save_incident;
    EditText inc_lat_editTextView;
    EditText inc_long_editTextView;
    EditText inc_cat_editTextView;
    IncidentList list = new IncidentList();


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

        //ShowAlert("Pop up", "All the views and DBHandler object initialised");

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

    public void buttonSaveIncident(View view){
        Incident incident = new Incident();
        incident.setLatitude(Double.valueOf(inc_lat_editTextView.getText().toString()));
        incident.setLongitude(Double.valueOf(inc_long_editTextView.getText().toString()));
        incident.setCategory(inc_cat_editTextView.getText().toString());
        db.addIncident(incident, this);
        Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();
        inc_lat_editTextView.setText("");
        inc_long_editTextView.setText("");
        inc_cat_editTextView.setText("");
        //list.populateListView();
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
