package com.greylabs.sumod.dbct10;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class IncidentDetails extends ActionBarActivity {
    DBHandler db;
    IncidentList list;

    public void buttonDeleteIncident(View view){
        Intent intent = getIntent();
        int incident_id = intent.getIntExtra("incident_id", 0);
        db.deleteIncdient(incident_id);
        Toast.makeText(this, "DELETED!", Toast.LENGTH_LONG).show();

        TextView inc_lat_view = (TextView) findViewById(R.id.inc_lat_view);
        TextView inc_long_view = (TextView) findViewById(R.id.inc_long_view);
        TextView inc_cat_view = (TextView) findViewById(R.id.inc_cat_view);

        inc_lat_view.setText("");
        inc_long_view.setText("");
        inc_cat_view.setText("");
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

        TextView inc_lat_view = (TextView) findViewById(R.id.inc_lat_view);
        TextView inc_long_view = (TextView) findViewById(R.id.inc_long_view);
        TextView inc_cat_view = (TextView) findViewById(R.id.inc_cat_view);

        inc_lat_view.setText(String.valueOf(incident.getLatitude()));
        inc_long_view.setText(String.valueOf(incident.getLongitude()));
        inc_cat_view.setText(String.valueOf(incident.getCategory()));
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
