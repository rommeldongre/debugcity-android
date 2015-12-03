package com.greylabs.sumod.dbct10.Fragments.Charts;

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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.greylabs.sumod.dbct10.R;
import com.greylabs.sumod.dbct10.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumod on 03-Dec-15.
 */
public class BarChart2Fragment extends Fragment {

    private static String TAG = "BarChart2Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar_chart2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new PopulateCharts().execute();
    }


    private class PopulateCharts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            ((ProgressBar) getActivity().findViewById(R.id.progressBar2)).setVisibility(View.VISIBLE);
            ((ProgressBar) getActivity().findViewById(R.id.progressBar2)).animate();


            ((BarChart) getActivity().findViewById(R.id.chart2)).setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            populateChart2();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((BarChart) getActivity().findViewById(R.id.chart2)).setVisibility(View.VISIBLE);

            ((ProgressBar) getActivity().findViewById(R.id.progressBar2)).setEnabled(false);
            ((ProgressBar) getActivity().findViewById(R.id.progressBar2)).setVisibility(View.GONE);

            super.onPostExecute(aVoid);
        }
    }


    public void populateChart2() {

        BarChart chart2 = (BarChart) getActivity().findViewById(R.id.chart2);

        WebService webService = new WebService(getActivity());

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
}
