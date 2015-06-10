package com.greylabs.sumod.dbct10;

/**
 * Created by Sumod on 6/3/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    //DATABASE VERSION and  NAME :
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DBugCT.db";
    private static final int zero = 0;
    //Incident Table:
    public static final String TABLE_INCIDENTS = "INCIDENTS";

    public static final String KEY_ID = "ID";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_CATEGORY = "CATEGORY";

    //Category Table:
    public static final String TABLE_CATEGORY = "CATEGORY";

    public static final String KEY_NAME = "NAME";
    public static final String KEY_DESCRIPTION = "DESCRIPTION";


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_INCIDENTS = "CREATE TABLE " + TABLE_INCIDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + KEY_LATITUDE + " VARCHAR(255) , "
                + KEY_LONGITUDE + " VARCHAR(255), "
                + KEY_CATEGORY + " VARCHAR(255) " + ")";
        db.execSQL(CREATE_TABLE_INCIDENTS);

        String CREATE_TABLE_CATEGORY = " CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_NAME + " VARCHAR(255), "
                + KEY_DESCRIPTION + " VARCHAR(255) " + ")";
        db.execSQL(CREATE_TABLE_CATEGORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_INCIDENTS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CATEGORY);

        onCreate(db);

    }

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Delete all tables:
    public void resetDatabase(){
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCIDENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        onCreate(db);

    }

    //CRUD operations:

    //Create a new row:

    public void addIncident(Incident incident, Context context) {
        //1
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_LATITUDE, incident.getLatitude());
            values.put(KEY_LONGITUDE, incident.getLongitude());
            values.put(KEY_CATEGORY, incident.getCategory());

            long k = db.insertOrThrow(TABLE_INCIDENTS, null, values);
            ShowAlert("db.insertOrThrow Returns:", String.valueOf(k), context);
            db.close();
        }
        catch(SQLiteException e){
            ShowAlert("Exception Caught", e.getMessage(), context);
        }
        catch(SQLException e){
            ShowAlert("Exception Caught", e.getMessage(), context);
        }
    }

    public void addCategory(Category category, Context context) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, category.getName());
            values.put(KEY_DESCRIPTION, category.getDescription());

            long k = db.insertOrThrow(TABLE_CATEGORY, null, values);
            ShowAlert("db.insertOrThrow returns", String.valueOf(k), context);
            db.close();
        }
        catch(SQLiteException e){
            ShowAlert("Exception Caught", e.getMessage(), context);
        }
        catch(SQLException e){
            ShowAlert("Exception Caught", e.getMessage(), context);
        }
    }

    //Reading a row:

    public Incident getIncident(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_INCIDENTS, new String[]{KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_CATEGORY,}, KEY_ID + "=?",
                new String[]{String.valueOf(_id)}, null, null, null, null);


        Incident incident = new Incident(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3));
        return incident;

    }

    public Category getCategory(String NAME) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_CATEGORY, new String[]{KEY_NAME, KEY_DESCRIPTION}, KEY_NAME + "=?", new String[]{NAME}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Category category = new Category(cursor.getString(0), cursor.getString(1));
        return category;
    }

    //get all incidents in a hashmap to display in a listview.
    public ArrayList<HashMap<String, String>> getIncidentList() {
        //Open connection to read only
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  " +
                KEY_LATITUDE + "," +
                KEY_LONGITUDE + "," +
                KEY_CATEGORY +
                " FROM " + TABLE_INCIDENTS;

        ArrayList<HashMap<String, String>> incidentList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> incident = new HashMap<String, String>();
                incident.put("latitude", cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                incident.put("longitude", cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                incident.put("category", cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                incidentList.add(incident);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return incidentList;

    }

    //get all categories in a hashmap to display on a listview.
    public ArrayList<HashMap<String, String>> getCategoryList(Context context) {
        //Open connection to read only
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  " +
                KEY_NAME + ", " +
                KEY_DESCRIPTION +
                " FROM " + TABLE_CATEGORY;

        ArrayList<HashMap<String, String>> categoryList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> category = new HashMap<String, String>();
                category.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                category.put("description", cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                categoryList.add(category);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryList;

    }


    //Updating a row:
    public void editIncident(Incident incident) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, incident.getLatitude());
        values.put(KEY_LONGITUDE, incident.getLongitude());
        values.put(KEY_CATEGORY, incident.getCategory());

        db.update(TABLE_INCIDENTS, values, KEY_ID + "=?", new String[]{String.valueOf(incident.get_id())});
        db.close();

    }

    public void editCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, category.getName());
        values.put(KEY_DESCRIPTION, category.getDescription());

        db.update(TABLE_CATEGORY, values, KEY_NAME + "=?", new String[]{category.getName()});
        db.close();
    }

    //getAllRows:

    public Cursor getAllIncidents() {
        SQLiteDatabase db = getReadableDatabase();
        String where = null;
        String[] ALL_KEYS = new String[]{KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_CATEGORY};
        Cursor cursor = db.query(true, TABLE_INCIDENTS, ALL_KEYS, where, null, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }



    public Cursor getAllCategories() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_CATEGORY, null, null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    //Deleting a row.
    public void deleteIncdient(int _id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //  db.execSQL("DELETE FROM" + TABLE_INCIDENTS + "WHERE" + KEY_ID + "=" + new String[]{String.valueOf(_id)});

        db.delete(TABLE_INCIDENTS, KEY_ID + "=?", new String[]{String.valueOf(_id)});
        db.close();

    }

    public void deleteCategory(String NAME) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CATEGORY, KEY_NAME + "=?", new String[]{NAME});
        db.close();
    }

    /**
     * DEBUGGING RELATED FUNCTIONS HERE:............
     * ...............................................................
     * ...............................................................
     */

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

    /* public void addIncident(Incident incident, Context context) {
        ShowAlert("Pop up", "addIncident function begins", context);


        try {

            SQLiteDatabase db = getWritableDatabase();//problem
        }
        catch (SQLiteException e){
            ShowAlert("SQL Error", e.getMessage() , context);
        }


        //1
        SQLiteDatabase db = getWritableDatabase();
        ShowAlert("Pop up", "1", context);


        ContentValues values = new ContentValues();
        ShowAlert("Pop up", "2", context);

        //values.put(KEY_ID, incident.get_id());

        //3
        values.put(KEY_LATITUDE, incident.getLatitude());
        ShowAlert("Pop up", "3", context);

        //4
        values.put(KEY_LONGITUDE, incident.getLongitude());
        ShowAlert("Pop up", "4", context);

        //5
        values.put(KEY_CATEGORY, incident.getCategory());
        ShowAlert("Pop up", "5", context);

        //6
        db.insert(TABLE_INCIDENTS, null, values);
        //ShowAlert("Pop up", "6", "OK", context);

        //7
        db.close();
        ShowAlert("Pop up", "7", context);

    }*/

    public void editIncident(Incident incident, Context context) {
        //1
        SQLiteDatabase db = this.getWritableDatabase();
        ShowAlert("Pop up", "1", context);

        //2
        ContentValues values = new ContentValues();
        ShowAlert("Pop up", "2", context);

        //3
        values.put(KEY_LATITUDE, incident.getLatitude());
        ShowAlert("Pop up", "3", context);

        //4
        values.put(KEY_LONGITUDE, incident.getLongitude());
        ShowAlert("Pop up", "4", context);

        //5
        values.put(KEY_CATEGORY, incident.getCategory());
        ShowAlert("Pop up", "5", context);

        //6
        db.update(TABLE_INCIDENTS, values, KEY_ID + "=?", new String[]{String.valueOf(incident.get_id())});
        ShowAlert("Pop up", "6", context);

        //7
        db.close();
        ShowAlert("Pop up", "7", context);

    }

    /*
    public Cursor getAllIncidents(Context context) {
        String where = "null";

        Cursor cursor = db.query(true, TABLE_INCIDENTS, null, null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }
    */


}
