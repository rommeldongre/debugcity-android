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
import android.os.StrictMode;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
                        final Incident incident = new Incident();
                        incident.setLatitude(Double.valueOf(user_lat_editTextView.getText().toString()));
                        incident.setLongitude(Double.valueOf(user_long_editTextView.getText().toString()));
                        incident.setCategory(spinner.getSelectedItem().toString());
                        incident.setPin_code(UserAdd.this);
                        bitmap = ((BitmapDrawable)user_imageView.getDrawable()).getBitmap();
                        incident.setImage(bitmap);
                        db.addIncident(incident, UserAdd.this);
                        Toast.makeText(UserAdd.this, "SAVED", Toast.LENGTH_SHORT).show();

                        Thread thread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    //Your code goes here
                                    //adding to web mySQL database
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("lat", Double.valueOf(user_lat_editTextView.getText().toString()));
                                        jsonObject.put("lng", Double.valueOf(user_long_editTextView.getText().toString()));
                                        jsonObject.put("cat", spinner.getSelectedItem().toString());
                                        jsonObject.put("pic", "none");
                                        jsonObject.put("locality", incident.getPin_code());
                                        //jsonObject.put("submitter", "not defined");
                                        //jsonObject.put("owner", "not defined");
                                        //jsonObject.put("state", "not defined");
                                        //jsonObject.put("severity", 3);
                                        //jsonObject.put("notes", "note");
                                        //jsonObject.put("votes", 1);

                                        HttpURLConnection httpcon;
                                        String url = "http://frrndlease.com/dbctv1/service/SubmitBug";
                                        String data = jsonObject.toString();
                                        String result = null;
                                        try{
//Connect
                                            httpcon = (HttpURLConnection) new URL(url).openConnection();
                                            httpcon.setDoOutput(true);
                                            httpcon.setRequestProperty("Content-Type", "application/json");
                                            httpcon.setRequestProperty("Accept", "application/json");
                                            httpcon.setRequestMethod("POST");
                                            httpcon.connect();

//Write
                                            OutputStream os = httpcon.getOutputStream();
                                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                                            writer.write(data);
                                            writer.close();
                                            os.close();

//Read
                                            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(),"UTF-8"));

                                            String line = null;
                                            StringBuilder sb = new StringBuilder();

                                            while ((line = br.readLine()) != null) {
                                                sb.append(line);
                                            }

                                            br.close();
                                            result = sb.toString();

                                            JSONObject response = new JSONObject(result);
                                            ShowAlert("Response:", result, UserAdd.this);

                                        } catch (UnsupportedEncodingException e) {
                                            ShowAlert("UnsupportedEncodingException", e.getMessage(), UserAdd.this);
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            ShowAlert("IOException", e.getMessage(), UserAdd.this);
                                            e.printStackTrace();
                                        }
                                    } catch (JSONException e) {
                                        ShowAlert("JSONException", e.getMessage(), UserAdd.this);
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();



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

