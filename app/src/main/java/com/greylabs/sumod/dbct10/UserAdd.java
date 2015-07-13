package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.media.MediaItemMetadata;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UserAdd extends ActionBarActivity {

    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    DBHandler db;
    TextView user_lat_editTextView;
    TextView user_long_editTextView;
    Spinner spinner;
    ImageView user_imageView;
    Bitmap bitmap;
    double latitude;
    double longitude;

    public void buttonSelectImage(View view){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(i, "Select Picture"),
                SELECT_PICTURE);
    }

    public void buttonUserSave(View view){

        new android.app.AlertDialog.Builder(this)
                .setTitle("Save")
                .setMessage("Save this Incident?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // adding to local database
                        Incident incident = new Incident();
                        incident.setLatitude(Double.valueOf(user_lat_editTextView.getText().toString()));
                        incident.setLongitude(Double.valueOf(user_long_editTextView.getText().toString()));
                        incident.setCategory(spinner.getSelectedItem().toString());
                        incident.setPin_code(UserAdd.this);
                        bitmap = ((BitmapDrawable)user_imageView.getDrawable()).getBitmap();
                        incident.setImage(bitmap);
                        db.addIncident(incident, UserAdd.this);
                        Toast.makeText(UserAdd.this, "SAVED", Toast.LENGTH_SHORT).show();

                        //adding to web mySQL database
                        RequestParams params = new RequestParams();
                        params.put("lat", 10);
                        params.put("lng", 10);
                        params.put("cat", "traffic");
                        params.put("pic", "none");
                        params.put("locality", 400007);
                        params.put("submitter", "rommel");
                        params.put("owner", "IPS");
                        params.put("state", "open");
                        params.put("severity", 3);
                        params.put("notes", "note");
                        params.put("votes", 1);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("token", 0);
                            invokeWS(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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

    private void invokeWS(JSONObject jsonObject) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://frrndlease.com/dbctv1/service/GetLocations", jsonObject, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String response){
                try {
                    JSONObject obj = new JSONObject(response);

                    ShowAlert("Response:", obj.get("returnCode") + "\n"
                            + obj.get("count") + "\n"
                            + obj.get("locality") + "\n"
                            + obj.get("returnToken"), UserAdd.this);

                    /*
                    if (obj.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "You are successfully registered!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), obj.getString("errorString"), Toast.LENGTH_LONG).show();
                    }
                    */



                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    ShowAlert("JSONException:", e.getMessage(), UserAdd.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content){
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                Toast.makeText(getApplicationContext(), "StatusCode: " + String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath);
                user_imageView.setVisibility(View.VISIBLE);
                user_imageView.setImageURI(selectedImageUri);
            }
        }
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
        int resultCode = intent.getIntExtra("resultCode", 2);
        switch (resultCode){
            case SELECT_PICTURE:
                Uri imageUri = intent.getParcelableExtra("ImageUri");
                user_imageView.setImageURI(imageUri);
                break;

            case REQUEST_IMAGE_CAPTURE:
                Bitmap photo = intent.getParcelableExtra("Photo");
                user_imageView.setImageBitmap(photo);
                break;
        }


        //user_imageView.setImageBitmap(photo);
        List<String> categoryList = db.getCategoryList(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        spinner.setAdapter(adapter);

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
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

    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getPincode(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String pincode = addressList.get(0).getPostalCode();

        return pincode;
    }
}

