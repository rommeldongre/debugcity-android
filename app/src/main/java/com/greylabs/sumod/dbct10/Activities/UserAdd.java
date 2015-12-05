package com.greylabs.sumod.dbct10.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
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

import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.GPSTracker;
import com.greylabs.sumod.dbct10.Model.Incident;
import com.greylabs.sumod.dbct10.PrefManager;
import com.greylabs.sumod.dbct10.R;
import com.greylabs.sumod.dbct10.WebService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UserAdd extends ActionBarActivity {

    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    DBHandler db;
    //WebService webService = new WebService();
    Incident incident = new Incident();
    TextView user_lat_editTextView;
    TextView user_long_editTextView;
    TextView user_pincode_editTextView;
    Spinner spinner;
    ImageView user_imageView;
    Bitmap bitmap;
    double latitude;
    double longitude;
    String pin_code;
    WebService webService = new WebService(this);

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

                        incident.setLatitude(latitude);
                        incident.setLongitude(longitude);
                        incident.setCategory(spinner.getSelectedItem().toString());

                        if (new PrefManager(getApplicationContext()).getEmail() != null) {
                            incident.setSubmitter(new PrefManager(getApplicationContext()).getEmail());
                        }
                        else {
                            incident.setSubmitter(new PrefManager(getApplicationContext()).getDeviceID());
                        }
                        incident.setPin_code(pin_code);
                        Toast.makeText(UserAdd.this, incident.getPin_code(), Toast.LENGTH_LONG).show();
                        bitmap = ((BitmapDrawable)user_imageView.getDrawable()).getBitmap();
                        incident.setImage(bitmap);
                        db.addIncident(incident, UserAdd.this);

                        new SubmitBugAsync().execute(incident);

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

    private class SubmitBugAsync extends AsyncTask<Incident, Void, Integer>{

        @Override
        protected Integer doInBackground(Incident... params) {
            return webService.SubmitBug(params[0]);
        }

        @Override
        protected void onPostExecute(Integer returnCode) {
            if (returnCode!=0)
                ShowAlert("ReturnCode:", String.valueOf(returnCode), UserAdd.this);
            if (returnCode == 0) {
                Toast.makeText(UserAdd.this, "SAVED", Toast.LENGTH_SHORT).show();
            }
        }
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
        user_pincode_editTextView = (TextView) findViewById(R.id.user_pincode_editTextView);
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //user_imageView.setImageBitmap(photo);
        List<String> categoryList = webService.getCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        spinner.setAdapter(adapter);

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            pin_code = addressList.get(0).getPostalCode();


            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            double shortLat = Double.valueOf(df.format(latitude));
            double shortLng = Double.valueOf(df.format(longitude));

            user_lat_editTextView.setText(String.valueOf(shortLat));
            user_long_editTextView.setText(String.valueOf(shortLng));
            user_pincode_editTextView.setText(pin_code);

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

