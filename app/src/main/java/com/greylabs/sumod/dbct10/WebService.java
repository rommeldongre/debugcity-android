package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumod on 14-Jul-15.
 */
public class WebService {

    public void SubmitBug(Incident incident){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", incident.getLatitude());
            jsonObject.put("lng", incident.getLongitude());
            jsonObject.put("cat", incident.getCategory());
            jsonObject.put("pic", "none");
            jsonObject.put("locality", incident.getPin_code());
            jsonObject.put("submitter", "not defined");
            jsonObject.put("owner", "not defined");
            jsonObject.put("state", "not defined");
            jsonObject.put("severity", 3);
            jsonObject.put("notes", "note");
            jsonObject.put("votes", 1);

            String url = "http://frrndlease.com/dbctv1/service/SubmitBug";
            String data = jsonObject.toString();
            String result;

            HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("POST");
            httpcon.connect();

            //write
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
            httpcon.disconnect();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLocations(Context context){

        String url = "http://frrndlease.com/dbctv1/service/GetLocations";
        List<String> pincodes = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        int returnToken = 0;


        try {
            do {
                HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
                httpcon.setDoOutput(true);
                httpcon.setRequestProperty("Content-Type", "application/json");
                httpcon.setRequestProperty("Accept", "application/json");
                httpcon.setRequestMethod("POST");
                httpcon.connect();


                jsonObject.put("token", returnToken);
                String data = jsonObject.toString();

                //ShowAlert("ReturnToken:", String.valueOf(returnToken), context);

                //write
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
                String result = sb.toString();

                JSONObject jsonResponse = new JSONObject(result);

                String locality = jsonResponse.getString("locality");
                String[] localities = locality.split(",");
                for (int i=0; i<localities.length; i++){
                    localities[i] = localities[i].replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]","");
                    pincodes.add(localities[i]);
                }

                returnToken = jsonResponse.getInt("returnToken");

                httpcon.disconnect();
            }while(returnToken!=0);




        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return pincodes;
    }

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
}
