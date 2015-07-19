package com.greylabs.sumod.dbct10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonIOException;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    DBHandler db;
    ListView mainListView;
    ViewFlipper viewFlipper;
    BarChart chart1;
    BarChart chart2;
    RadarChart chart3;
    WebService webService = new WebService(this);

    private static final int SELECT_PICTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

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

    public void gotoMapsActivity(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
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
        //RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        db = new DBHandler(this, null, null, 1);
        chart1 = (BarChart) findViewById(R.id.chart1);
        chart2 = (BarChart) findViewById(R.id.chart2);
        chart3 = (RadarChart) findViewById(R.id.chart3);

        ((ViewGroup) chart1.getParent()).removeView(chart1);
        ((ViewGroup) chart2.getParent()).removeView(chart2);
        ((ViewGroup) chart3.getParent()).removeView(chart3);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        viewFlipper.addView(chart1);
        viewFlipper.addView(chart2);
        viewFlipper.addView(chart3);
        populateChart1();
        //populateChart2();
        populateChart3();

        //MyTest();
    }

    @Override
    protected void onResume() {
        populateChart1();
        populateChart2();
        populateChart3();
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

        if (id == R.id.adminButton) {
            Intent i = new Intent(this, AdminMenu.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
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
        if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            System.out.println("Image Path : " + selectedImagePath);
            Intent i = new Intent(MainActivity.this, UserAdd.class);
            i.putExtra("ImageUri", selectedImageUri);
            i.putExtra("resultCode", SELECT_PICTURE);
            startActivity(i);
        }
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<String> pincodes = webService.getLocations();
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
        chart1.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
        chart1.setDescription("# of Incidents vs PinCode");
        chart1.setDescriptionColor(getResources().getColor(R.color.abc_primary_text_material_dark));

        //Styling
        Legend legend = chart1.getLegend();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.material_blue_grey_800);
        legend.setColors(colors);
        chart1.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
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
        chart1.animateY(3000);
    }

    public void populateChart2() {
        Cursor cursor = db.getCursorByRawQuery("SELECT ID, CATEGORY, COUNT(*) as C FROM INCIDENTS GROUP BY CATEGORY ORDER BY C DESC");
        int count = cursor.getCount();

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < count; i++) {
            barEntries.add(new BarEntry(cursor.getInt(cursor.getColumnIndex("C")), i));
            cursor.moveToNext();
        }

        BarDataSet barDataset = new BarDataSet(barEntries, "# of Incidents");

        ArrayList<String> labels = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < count; i++) {
            labels.add(cursor.getString(cursor.getColumnIndex("CATEGORY")));
            cursor.moveToNext();
        }

        BarData data = new BarData(labels, barDataset);
        chart2.setData(data);

        chart2.setDescription("# of Incidents vs Category");
        chart2.setDescriptionColor(getResources().getColor(R.color.material_blue_grey_800));

        //Styling
        chart2.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
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
        chart1.animateY(3000);
    }

    public void populateChart3() {

        List<String> pincodes = webService.getLocations();
        List<String> categories = webService.getCategories();

        GPSTracker gps = new GPSTracker(this);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        String pin_code = "Unknown";
        try {
            addressList = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
            if (addressList.size() != 0) {
                pin_code = addressList.get(0).getPostalCode();
            }
            else
                pin_code = "Unknown";
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        JSONObject present_LocationVector = webService.getLocationVector("411038");
        ShowAlert("Present LocationVector: ", present_LocationVector.toString());
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

                    if (locationVectors.get(i).has(categories.get(j)) && locationVectors.get(i).getInt(categories.get(j)) >= ideal_score){
                        ideal_score = locationVectors.get(i).getInt(categories.get(j));
                    }
                }
                yVals2.add(new Entry(ideal_score, j));

                if (present_LocationVector.has(categories.get(j))){
                    yVals1.add(new Entry(present_LocationVector.getInt(categories.get(j)), j));
                    ShowAlert("presentValues:", String.valueOf(present_LocationVector.getInt(categories.get(j))));
                }

                xVals.add(categories.get(j));
                //ShowAlert("Ideal Score:", categories.get(j) + ": " + ideal_score);
            }



            RadarDataSet radarDataSet1 = new RadarDataSet(yVals1, "Fun");
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
            chart3.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
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
        try {


            for (int i = 0; i < localities.size(); i++) {
                ShowAlert("Pincode:", localities.get(i));
                Log.i(TAG, "Localities: " + localities.get(i));
            }
        }
        catch (JsonIOException e){
            Log.e(TAG, e.getMessage());
        }
    }



}









