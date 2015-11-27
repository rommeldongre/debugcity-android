package com.greylabs.sumod.dbct10.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.login.LoginManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.greylabs.sumod.dbct10.Charts;
import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.GPSTracker;
import com.greylabs.sumod.dbct10.MyMarkerView;
import com.greylabs.sumod.dbct10.PrefManager;
import com.greylabs.sumod.dbct10.R;
import com.greylabs.sumod.dbct10.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = "MainActivity";
    private Toolbar mToolbar;
    private PrefManager pref;
    DBHandler db;
    ListView mainListView;
    ViewFlipper viewFlipper;
    BarChart chart1;
    BarChart chart2;
    RadarChart chart3;
    Charts charts;
    ProgressBar progressbar_spinner;
    WebService webService = new WebService(this);
    Button button_flip;
    double latitude;
    double longitude;

    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private class PopulateCharts extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {

            progressbar_spinner.setVisibility(View.VISIBLE);
            progressbar_spinner.animate();

            button_flip.setVisibility(View.GONE);
            button_flip.setEnabled(false);

            chart1 = (BarChart) findViewById(R.id.chart1);
            chart1.setVisibility(View.GONE);
            chart2 = (BarChart) findViewById(R.id.chart2);
            chart2.setVisibility(View.GONE);
            chart3 = (RadarChart) findViewById(R.id.chart3);
            chart3.setVisibility(View.GONE);


            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            populateChart1();
            populateChart2();
            populateChart3(params[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            chart1.setVisibility(View.VISIBLE);
            chart2.setVisibility(View.VISIBLE);
            chart3.setVisibility(View.VISIBLE);
            progressbar_spinner.setEnabled(false);
            progressbar_spinner.setVisibility(View.GONE);

            button_flip.setVisibility(View.VISIBLE);
            button_flip.setEnabled(true);

            ((ViewGroup) chart1.getParent()).removeView(chart1);
            ((ViewGroup) chart2.getParent()).removeView(chart2);
            ((ViewGroup) chart3.getParent()).removeView(chart3);

            viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
            viewFlipper.addView(chart3);
            viewFlipper.addView(chart1);
            viewFlipper.addView(chart2);
            super.onPostExecute(aVoid);
        }
    }

    public void buttonSelectImage(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(i, "Select Picture"),
                SELECT_PICTURE);
    }

    public void buttonShoot(View view) {
        if (!hasCamera())
            Toast.makeText(this, "No camera on device", Toast.LENGTH_SHORT).show();
        else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void flipToNext(View view) {
        int count = viewFlipper.getChildCount();
        int displayedChildIndex = viewFlipper.getDisplayedChild();
        if (displayedChildIndex != (count - 1)) {
            viewFlipper.setDisplayedChild(displayedChildIndex + 1);
        } else
            viewFlipper.setDisplayedChild(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout)
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        pref = new PrefManager(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBHandler(this, null, null, 1);

        button_flip = (Button) findViewById(R.id.button_flip);

        progressbar_spinner = (ProgressBar)findViewById(R.id.progressBar1);

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        else{
            gps.showSettingsAlert(this);
        }

        LatLng latLng = new LatLng(latitude, longitude);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            //ShowAlert("Address:", addressList.get(0).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pin_code = addressList.get(0).getPostalCode();

        new PopulateCharts().execute(pin_code);

        //((ViewGroup) chart1.getParent()).removeView(chart1);
        //((ViewGroup) chart2.getParent()).removeView(chart2);
        //((ViewGroup) chart3.getParent()).removeView(chart3);

        //viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        //viewFlipper.addView(chart3);
        //viewFlipper.addView(chart1);
        //viewFlipper.addView(chart2);

        //populateChart1();
        //populateChart2();
        //populateChart3();

        //MyTest();
    }

    @Override
    protected void onResume() {
        //populateChart1();
        //populateChart2();
        //populateChart3();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if (id == R.id.mapsActivity){
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        }

        if (id == R.id.admin){
            Intent i = new Intent(this, AdminMenu.class);
            startActivity(i);
        }

        if (id == R.id.btn_logout){

            if (pref.getLoginSessionCode() == pref.EMAIL_LOGIN_SESSION){
                logoutWithEmail();
            }
            else if(pref.getLoginSessionCode() == pref.GOOGLE_LOGIN_SESSION){
                logoutWithGoogle();
            }
            else if (pref.getLoginSessionCode() == pref.FB_LOGIN_SESSION){
                logoutWtihFacebook();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutWithEmail(){
        pref.logout();
        Intent i = new Intent(MainActivity.this, ActivityLogin.class);
        startActivity(i);
        finish();
    }

    private void logoutWithGoogle(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        pref.logout();
                        Intent i = new Intent(MainActivity.this, ActivityLogin.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    private void logoutWtihFacebook(){
        LoginManager.getInstance().logOut();
        pref.logout();
        Intent i = new Intent(MainActivity.this, ActivityLogin.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            Intent i = new Intent(MainActivity.this, UserAdd.class);
            i.putExtra("Photo", photo);
            i.putExtra("resultCode", REQUEST_IMAGE_CAPTURE);
            startActivity(i);
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            Intent i = new Intent(MainActivity.this, UserAdd.class);
            i.putExtra("ImageUri", selectedImageUri);
            i.putExtra("resultCode", SELECT_PICTURE);
            startActivity(i);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //converting bitmap to byte[] and vice-versa:
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getByteArrayAsBitmap(byte[] imgByte) {
        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }

    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void ShowAlert(String title, String message, String button) {
        AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });
        alertDialog.setIcon(R.drawable.abc_dialog_material_background_dark);
        alertDialog.show();
    }

    public void populateListView() {
        Cursor cursor = db.getCursorByRawQuery("SELECT ID, PINCODE as _id, COUNT(*) as C FROM INCIDENTS GROUP BY PINCODE ORDER BY C DESC");

        String[] fromFeildNames = new String[]{"_id", "C"};
        int[] toViewIDs = new int[]{android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                cursor, fromFeildNames, toViewIDs, 0);
        mainListView.setAdapter(myCursorAdapter);
    }


    public void populateChart1() {

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        final List<String> pincodes = webService.getLocations();
        List<String> categories = webService.getCategories();

        List<JSONObject> locationVectors = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        try {
            for (int i = 0; i < pincodes.size(); i++) {
                locationVectors.add(webService.getLocationVector(pincodes.get(i)));
            }

            for (int i = 0; i < locationVectors.size(); i++) {
                int total_incidents = 0;
                for (int j = 0; j < categories.size(); j++) {

                    if (locationVectors.get(i).has(categories.get(j))) {
                        total_incidents = total_incidents + locationVectors.get(i).getInt(categories.get(j));
                    }
                }
                barEntries.add(new BarEntry(total_incidents, i));

            }

        }
        catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }

        BarDataSet barDataset = new BarDataSet(barEntries, "# of Incidents");

        ArrayList<String> labels = new ArrayList<String>();

        for (int i=0; i<pincodes.size(); i++){
            labels.add(pincodes.get(i));
        }

        BarData data = new BarData(labels, barDataset);
        chart1.setData(data);
        //Bday - chart1.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
        chart1.setDescription("# of Incidents vs PinCode");
        chart1.setDescriptionColor(getResources().getColor(R.color.abc_primary_text_material_dark));

        //Styling
        Legend legend = chart1.getLegend();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.material_blue_grey_800);
        legend.setColors(colors);
        //Bday - chart1.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        barDataset.setColor(getResources().getColor(R.color.material_blue_grey_800));

        XAxis xAxis = chart1.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisLeft = chart1.getAxisLeft();
        yAxisLeft.setEnabled(false);
        yAxisLeft.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisRight = chart1.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        xAxis.setDrawGridLines(false);

        chart1.setDescriptionColor(getResources().getColor(R.color.material_blue_grey_800));

        chart1.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                String pin_code = pincodes.get(entry.getXIndex());
                Intent intent = new Intent(MainActivity.this, SpiderChartActivity.class);
                intent.putExtra("pin_code", pin_code);
                Log.i(TAG, pin_code);

                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        //chart1.animateY(3000);
    }

    public void populateChart2() {

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        List<String> categories = webService.getCategories();
        List<String> pincodes = webService.getLocations();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        List<JSONObject> locationVectors = new ArrayList<>();
        int total_incidents;


        try {
            for (int i = 0; i < pincodes.size(); i++) {
                locationVectors.add(webService.getLocationVector(pincodes.get(i)));
            }

            for (int j=0; j<categories.size(); j++){
                total_incidents = 0;

                for (int i=0; i<locationVectors.size(); i++){
                    if (locationVectors.get(i).has(categories.get(j))) {
                        Log.i("Location: " + pincodes.get(i) + ": ", "Before: " + String.valueOf(total_incidents));
                        total_incidents = total_incidents + locationVectors.get(i).getInt(categories.get(j));
                        Log.i("Location: " + pincodes.get(i) + ": ", "After: " + String.valueOf(total_incidents));
                    }
                }
                barEntries.add(new BarEntry(total_incidents, j));
                labels.add(categories.get(j));
            }
        }
        catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }

        BarDataSet barDataset = new BarDataSet(barEntries, "# of Incidents");

        BarData data = new BarData(labels, barDataset);
        chart2.setData(data);

        chart2.setDescription("# of Incidents vs Category");
        chart2.setDescriptionColor(getResources().getColor(R.color.material_blue_grey_800));

        //Styling
        //Bday - chart2.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
        barDataset.setColor(getResources().getColor(R.color.material_blue_grey_800));

        Legend legend = chart2.getLegend();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.material_blue_grey_800);
        legend.setColors(colors);


        XAxis xAxis = chart2.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisLeft = chart2.getAxisLeft();
        yAxisLeft.setEnabled(false);
        yAxisLeft.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisRight = chart2.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
        xAxis.setDrawGridLines(false);
        //chart2.animateY(3000);

    }

    public void populateChart3(String pin_code) {

        List<String> pincodes = webService.getLocations();
        List<String> categories = webService.getCategories();

        //GPSTracker gps = new GPSTracker(this);

        //Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //List<Address> addressList;
        //String pin_code="null";
        //double latitude = latLng.latitude;
        //double longitude = latLng.longitude;

        //try {
        //    addressList = geocoder.getFromLocation(latitude, longitude, 1);
        //    if (addressList != null && addressList.size() != 0 && addressList.get(0).getPostalCode() != null){
        //        pin_code = addressList.get(0).getPostalCode();
        //    }
        //    else{
        //        pin_code = "Unknown";
        //    }
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}



        //ShowAlert("Pin_code: ", pin_code);

        JSONObject present_LocationVector = webService.getLocationVector(pin_code);
        Log.i(TAG, present_LocationVector.toString());

        List<JSONObject> locationVectors = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>(); //labels
        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<Entry> yVals2 = new ArrayList<>();

        try {
            for (int i = 0; i < pincodes.size(); i++) {
                locationVectors.add(webService.getLocationVector(pincodes.get(i)));
            }

            for (int j = 0; j<categories.size(); j++){
                int ideal_score = 0;
                for (int i = 0; i<locationVectors.size(); i++){
                    if (locationVectors.get(i).has(categories.get(j)) && locationVectors.get(i).getInt(categories.get(j)) > ideal_score){
                        ideal_score = locationVectors.get(i).getInt(categories.get(j));
                    }
                }
                yVals2.add(new Entry(ideal_score, j));

                if (present_LocationVector.has(categories.get(j))){
                    yVals1.add(new Entry(present_LocationVector.getInt(categories.get(j)), j));
                }

                xVals.add(categories.get(j));
                //ShowAlert("Ideal Score:", categories.get(j) + ": " + ideal_score);
            }



            RadarDataSet radarDataSet1 = new RadarDataSet(yVals1, pin_code);
            radarDataSet1.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);
            radarDataSet1.setDrawFilled(true);
            radarDataSet1.setLineWidth(2f);

            RadarDataSet radarDataSet2 = new RadarDataSet(yVals2, "Ideal");
            radarDataSet2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
            radarDataSet2.setDrawFilled(false);
            radarDataSet2.setLineWidth(2f);

            ArrayList<RadarDataSet> radarDataSet = new ArrayList<>();
            radarDataSet.add(radarDataSet1);
            radarDataSet.add(radarDataSet2);

            RadarData radarData = new RadarData(xVals, radarDataSet);

            radarData.setValueTextSize(8f);
            radarData.setDrawValues(false);

            chart3.setData(radarData);
            chart3.invalidate();

            chart3.setDescription("");

            MyMarkerView myMarkerView = new MyMarkerView(this, R.layout.custom_marker_view);
            chart3.setMarkerView(myMarkerView);

            //Styling
            //chart3.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
            chart3.setWebLineWidth(1.5f);
            chart3.setWebColor(getResources().getColor(R.color.abc_primary_text_material_dark));
            chart3.setWebColorInner(getResources().getColor(R.color.abc_primary_text_material_dark));
            chart3.setWebLineWidthInner(0.75f);
            chart3.setWebAlpha(100);

            XAxis xAxis = chart3.getXAxis();
            //xAxis.setTypeface(tf);
            xAxis.setTextSize(9f);
            xAxis.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));

            YAxis yAxis = chart3.getYAxis();
            yAxis.setEnabled(false);
            //yAxis.setTypeface(tf);
            yAxis.setLabelCount(5);
            yAxis.setTextSize(9f);
            yAxis.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
            yAxis.setStartAtZero(true);

            Legend legend = chart3.getLegend();
            legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            legend.setTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));
            //legend.setTypeface(tf);
            legend.setXEntrySpace(7f);
            legend.setYEntrySpace(5f);


        }catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }

    }

    public void ShowAlert(String title, String message) {
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


    public void MyTest(){
        List<String> localities;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        localities = webService.getLocations();


            for (int i = 0; i < localities.size(); i++) {
                ShowAlert("Pincode:", localities.get(i));
                Log.i(TAG, "Localities: " + localities.get(i));
            }
    }



}









