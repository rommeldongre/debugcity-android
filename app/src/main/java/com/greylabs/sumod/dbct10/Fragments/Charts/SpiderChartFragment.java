package com.greylabs.sumod.dbct10.Fragments.Charts;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.model.LatLng;
import com.greylabs.sumod.dbct10.GPSTracker;
import com.greylabs.sumod.dbct10.MyMarkerView;
import com.greylabs.sumod.dbct10.R;
import com.greylabs.sumod.dbct10.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sumod on 03-Dec-15.
 */
public class SpiderChartFragment extends Fragment {

    private static String TAG = "SpiderChartFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_spider_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        double latitude = 0; double longitude = 0;

        GPSTracker gps = new GPSTracker(getActivity());
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        else{
            gps.showSettingsAlert(getActivity());
        }


        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            //ShowAlert("Address:", addressList.get(0).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pin_code = addressList.get(0).getPostalCode();

        new PopulateCharts().execute(pin_code);
    }


    private class PopulateCharts extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

            ((ProgressBar) getActivity().findViewById(R.id.progressBar3)).setVisibility(View.VISIBLE);
            ((ProgressBar) getActivity().findViewById(R.id.progressBar3)).animate();


            ((RadarChart) getActivity().findViewById(R.id.chart3)).setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            populateChart3(params[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((RadarChart) getActivity().findViewById(R.id.chart3)).setVisibility(View.VISIBLE);

            ((ProgressBar) getActivity().findViewById(R.id.progressBar3)).setEnabled(false);
            ((ProgressBar) getActivity().findViewById(R.id.progressBar3)).setVisibility(View.GONE);

            super.onPostExecute(aVoid);
        }
    }

    public void populateChart3(String pin_code) {

        WebService webService = new WebService(getActivity());

        RadarChart chart3 = (RadarChart) getActivity().findViewById(R.id.chart3);

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

            MyMarkerView myMarkerView = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
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

}
