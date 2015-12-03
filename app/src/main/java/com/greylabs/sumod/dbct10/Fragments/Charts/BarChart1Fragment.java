package com.greylabs.sumod.dbct10.Fragments.Charts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.greylabs.sumod.dbct10.Activities.SpiderChartActivity;
import com.greylabs.sumod.dbct10.Activities.StartActivity;
import com.greylabs.sumod.dbct10.R;
import com.greylabs.sumod.dbct10.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumod on 03-Dec-15.
 */
public class BarChart1Fragment extends Fragment {

    private static final String TAG = "BarChart1Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar_chart1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new PopulateCharts().execute();

    }



    private class PopulateCharts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            ((ProgressBar) getActivity().findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
            ((ProgressBar) getActivity().findViewById(R.id.progressBar1)).animate();


            ((BarChart) getActivity().findViewById(R.id.chart1)).setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            populateChart1();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((BarChart) getActivity().findViewById(R.id.chart1)).setVisibility(View.VISIBLE);

            ((ProgressBar) getActivity().findViewById(R.id.progressBar1)).setEnabled(false);
            ((ProgressBar) getActivity().findViewById(R.id.progressBar1)).setVisibility(View.GONE);

            super.onPostExecute(aVoid);
        }
    }

    public void populateChart1() {
        WebService webService = new WebService(getActivity());
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        BarChart chart1 = (BarChart) getActivity().findViewById(R.id.chart1);

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
                Intent intent = new Intent(getActivity(), SpiderChartActivity.class);
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

}
