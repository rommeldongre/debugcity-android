package com.greylabs.sumod.dbct10;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;


public class IncidentList extends AppCompatActivity {
    DBHandler db;
    TextView incident_id;

    public void IncidentList(){

    }

    public void gotoIncidentAdd(View view){
        Intent i = new Intent(this, IncidentAdd.class);
        startActivity(i);
    }

    public void buttonRefreshIncidentList(View view){
        populateListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_list);
        //ShowAlert("Pop up", "OnCreate IncidentList");
        db = new DBHandler(this, null, null, 1);
        //ShowAlert("Pop up", "db initialised");
        populateListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incident_list, menu);
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



/*
    private void populateListView(){
        Cursor cursor = db.getAllIncidents();
        //ShowAlert("Pop up", "db.getAllIncidents");

        //1
        String[] fromFieldNames = new String[] {DBHandler.KEY_ID, DBHandler.KEY_LATITUDE, DBHandler.KEY_LONGITUDE, DBHandler.KEY_CATEGORY};
        ShowAlert("Popup","1");

        //2
        int[] toViewIDs = new int[] {R.id.incident_id, R.id.incident_latitude, R.id.incident_longitude, R.id.incident_category};
        ShowAlert("Popup", "2");

        //3
        SimpleCursorAdapter myCursorAdapter;
        ShowAlert("Pop up", "3");

        //4
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.incident_list_item, cursor, fromFieldNames, toViewIDs, 0);
        ShowAlert("Pop up", "4");

        //5
        ListView incidentlistview = (ListView) findViewById(R.id.incidentlistview);
        ShowAlert("Pop up", "5");

        //6
        incidentlistview.setAdapter(myCursorAdapter);
        ShowAlert("Pop up", "6");

    }
*/


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

    public void populateListView(){
        ArrayList<HashMap<String, String>> incidentList =  db.getIncidentList();
        ListView incidentlistview = (ListView) findViewById(R.id.incidentlistview);
        ListAdapter adapter = new SimpleAdapter( IncidentList.this, incidentList, R.layout.incident_list_item,
                new String[] {"id", "latitude","longitude","category"},
                new int[] {R.id.incident_id, R.id.incident_latitude, R.id.incident_longitude, R.id.incident_category});
        incidentlistview.setAdapter(adapter);
        incidentlistview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        incident_id = (TextView) view.findViewById(R.id.incident_id);
                        String incidentId = incident_id.getText().toString();
                        Intent objIntent = new Intent(getApplicationContext(),IncidentDetails.class);
                        objIntent.putExtra("incident_id", Integer.parseInt(incidentId));
                        startActivity(objIntent);

                    }
                }
        );
    }
}

