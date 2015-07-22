package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sumod on 18-Jul-15.
 */
public class Charts {

    private Context context;
    BarChart chart1;
    BarChart chart2;
    RadarChart chart3;
    WebService webService;
    private static final String TAG = "Charts";

    public Charts(Context context){
        this.context = context;
    }

    public BarChart populateChart1() {

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
                        Log.i(TAG, categories.get(j) + ": " + locationVectors.get(i).getInt(categories.get(j)));
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
        chart1.setBackgroundColor(context.getResources().getColor(R.color.material_blue_grey_800));
        chart1.setDescription("# of Incidents vs PinCode");
        chart1.setDescriptionColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));

        //Styling
        Legend legend = chart1.getLegend();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.material_blue_grey_800);
        legend.setColors(colors);
        chart1.setBackgroundColor(context.getResources().getColor(R.color.button_material_dark));
        barDataset.setColor(context.getResources().getColor(R.color.material_blue_grey_800));

        XAxis xAxis = chart1.getXAxis();
        xAxis.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisLeft = chart1.getAxisLeft();
        yAxisLeft.setEnabled(false);
        yAxisLeft.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisRight = chart1.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
        xAxis.setDrawGridLines(false);

        chart1.setDescriptionColor(context.getResources().getColor(R.color.material_blue_grey_800));
        chart1.animateY(3000);

        return chart1;
    }



    public BarChart populateChart2() {
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
                    if (locationVectors.get(i).has(categories.get(j)))
                        total_incidents = total_incidents + locationVectors.get(i).getInt(categories.get(j));
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
        chart2.setDescriptionColor(context.getResources().getColor(R.color.material_blue_grey_800));

        //Styling
        chart2.setBackgroundColor(context.getResources().getColor(R.color.button_material_dark));
        barDataset.setColor(context.getResources().getColor(R.color.material_blue_grey_800));

        Legend legend = chart2.getLegend();
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.material_blue_grey_800);
        legend.setColors(colors);


        XAxis xAxis = chart2.getXAxis();
        xAxis.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisLeft = chart2.getAxisLeft();
        yAxisLeft.setEnabled(false);
        yAxisLeft.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
        YAxis yAxisRight = chart2.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
        xAxis.setDrawGridLines(false);
        chart2.animateY(3000);


        return chart2;
    }



    public void populateChart3(){

        List<String> pincodes = webService.getLocations();
        List<String> categories = webService.getCategories();

        JSONObject present_LocationVector = webService.getLocationVector("411038");

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
                    if (locationVectors.get(i).has(categories.get(j))){
                        if (locationVectors.get(i).getInt(categories.get(j)) > ideal_score){
                            ideal_score = locationVectors.get(i).getInt(categories.get(j));
                            yVals2.add(new Entry(ideal_score, j));
                        }
                    }
                }
            }

            for (int i=0; i<categories.size(); i++){
                xVals.add(categories.get(i));
            }

            //RadarDataSet radarDataSet1 = new RadarDataSet(yVals1, pin_code);
            //radarDataSet1.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);
            //radarDataSet1.setDrawFilled(true);
            //radarDataSet1.setLineWidth(2f);

            RadarDataSet radarDataSet2 = new RadarDataSet(yVals2, "Ideal");
            radarDataSet2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
            radarDataSet2.setDrawFilled(false);
            radarDataSet2.setLineWidth(2f);

            ArrayList<RadarDataSet> radarDataSet = new ArrayList<>();
            //radarDataSet.add(radarDataSet1);
            radarDataSet.add(radarDataSet2);

            RadarData radarData = new RadarData(xVals, radarDataSet);

            radarData.setValueTextSize(8f);
            radarData.setDrawValues(false);

            chart3.setData(radarData);
            chart3.invalidate();

            chart3.setDescription("");

            MyMarkerView myMarkerView = new MyMarkerView(context, R.layout.custom_marker_view);
            chart3.setMarkerView(myMarkerView);

            //Styling
            chart3.setBackgroundColor(context.getResources().getColor(R.color.button_material_dark));
            chart3.setWebLineWidth(1.5f);
            chart3.setWebColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
            chart3.setWebColorInner(context.getResources().getColor(R.color.abc_primary_text_material_dark));
            chart3.setWebLineWidthInner(0.75f);
            chart3.setWebAlpha(100);

            XAxis xAxis = chart3.getXAxis();
            //xAxis.setTypeface(tf);
            xAxis.setTextSize(9f);
            xAxis.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));

            YAxis yAxis = chart3.getYAxis();
            yAxis.setEnabled(false);
            //yAxis.setTypeface(tf);
            yAxis.setLabelCount(5);
            yAxis.setTextSize(9f);
            yAxis.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
            yAxis.setStartAtZero(true);

            Legend legend = chart3.getLegend();
            legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            legend.setTextColor(context.getResources().getColor(R.color.abc_primary_text_material_dark));
            //legend.setTypeface(tf);
            legend.setXEntrySpace(7f);
            legend.setYEntrySpace(5f);


        }catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }



    }
/*
    public void populateChart1() {

        Cursor cursor = db.getCursorByRawQuery("SELECT ID, PINCODE, COUNT(*) as C FROM INCIDENTS GROUP BY PINCODE ORDER BY C DESC");
        int count = cursor.getCount();
        cursor.moveToFirst();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            barEntries.add(new BarEntry(cursor.getInt(cursor.getColumnIndex("C")), i));
            cursor.moveToNext();
        }

        BarDataSet barDataset = new BarDataSet(barEntries, "# of Incidents");

        ArrayList<String> labels = new ArrayList<String>();
        //labels.add("test");labels.add("test2");labels.add("test3");
        cursor.moveToFirst();
        for (int i = 0; i < count; i++) {
            labels.add(cursor.getString(cursor.getColumnIndex("PINCODE")));
            cursor.moveToNext();
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
        GPSTracker gps = new GPSTracker(this);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        String pin_code = null;
        try {
            addressList = geocoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
            if (addressList.size() != 0) {
                pin_code = addressList.get(0).getPostalCode();
            }
            if (pin_code == null) {
                pin_code = "Unknown";
            }
        } catch (IOException e) {
            e.printStackTrace();
            ShowAlert("Exception Caught:", e.getMessage());
        }

        //if (pin_code != "Unknown") {
        pin_code = "411038";
        Cursor cursor1 = db.getCursorByRawQuery("SELECT ID, PINCODE, CATEGORY, COUNT(*) as C FROM INCIDENTS " +
                "WHERE PINCODE = " + pin_code + " GROUP BY CATEGORY ORDER BY CATEGORY");

        int count = cursor1.getCount();

        ArrayList<Entry> yVals1 = new ArrayList<>();

        cursor1.moveToFirst();
        for (int i = 0; i < count; i++) {
            yVals1.add(new Entry(cursor1.getInt(cursor1.getColumnIndex("C")), i));
            cursor1.moveToNext();
        }

            /*
            *
            * to get ideal values:
             */
/*
        Cursor cursor = db.getCursorByRawQuery("SELECT CATEGORY, MAX(C) as MOST FROM(SELECT CATEGORY, C FROM(SELECT PINCODE, CATEGORY, COUNT(*) as C FROM INCIDENTS " +
                "GROUP BY PINCODE, CATEGORY ORDER BY CATEGORY) ORDER BY CATEGORY) GROUP BY CATEGORY");
        ArrayList<Entry> yVals2 = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i<cursor.getCount(); i++){
            yVals2.add(new Entry(cursor.getInt(cursor.getColumnIndex("MOST")), i));
            //ShowAlert(String.valueOf(i + 1) + ":", cursor.getString(cursor.getColumnIndex("CATEGORY")) + " | " +
            //      cursor.getString(cursor.getColumnIndex("MOST")));
            cursor.moveToNext();
        }


        ArrayList<String> xVals = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < count; i++) {
            xVals.add(cursor.getString(cursor.getColumnIndex("CATEGORY")));
            cursor.moveToNext();
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
        // }
        /*else {
            chart3.setNoDataText("No Pincode Data Available");
        }*/
 /*       try {

        } catch (SQLiteException e) {
            ShowAlert("Exception Caught:", e.getMessage());
        }


    }
*/
}
