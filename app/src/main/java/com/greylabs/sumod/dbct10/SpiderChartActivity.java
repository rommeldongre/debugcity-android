package com.greylabs.sumod.dbct10;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpiderChartActivity extends AppCompatActivity {

    public static final String TAG = "SpiderChartActivity";
    RadarChart chart3;
    WebService webService = new WebService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spider_chart);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        chart3 = (RadarChart) findViewById(R.id.spiderchart);
        Intent intent = getIntent();
        String pin_code = intent.getStringExtra("pin_code");
        Log.i(TAG, pin_code);
        populateChart3(pin_code);


    }

    private class PopulateSpiderChart extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            populateChart3(params[0]);
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spider_chart, menu);
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

            for (int j = 0; j < categories.size(); j++) {
                int ideal_score = 0;
                for (int i = 0; i < locationVectors.size(); i++) {
                    if (locationVectors.get(i).has(categories.get(j)) && locationVectors.get(i).getInt(categories.get(j)) > ideal_score) {
                        ideal_score = locationVectors.get(i).getInt(categories.get(j));
                    }
                }
                yVals2.add(new Entry(ideal_score, j));

                if (present_LocationVector.has(categories.get(j))) {
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


        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

    }

}

