package com.greylabs.sumod.dbct10;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.Model.Incident;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sumod on 14-Jul-15.
 */
public class WebService {
    private Context context;

    public WebService(Context context) {
        this.context = context;
    }

    DBHandler database = new DBHandler(context, null, null, 0);

    public int SubmitBug(Incident incident){
        JSONObject jsonObject = new JSONObject();
        int returncode = 1;
        try {
            jsonObject.put("lat", incident.getLatitude());
            jsonObject.put("lng", incident.getLongitude());
            jsonObject.put("cat", incident.getCategory());
            jsonObject.put("pic", encodeTobase64(incident.getImage()));
            jsonObject.put("locality", incident.getPin_code());
            jsonObject.put("submitter", incident.getSubmitter());
            jsonObject.put("owner", "not defined");
            jsonObject.put("state", "not defined");
            jsonObject.put("severity", 3);
            jsonObject.put("notes", "note");
            jsonObject.put("votes", 1);

            String url = "http://www.frrndlease.com/dbctv1/service/SubmitBug";
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

            returncode = response.getInt("returnCode");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return returncode;
    }

    public List<String> getCategories(){
        List<String> categories = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(" ", " ");

            String url = "http://www.frrndlease.com/dbctv1/service/GetCategories";
            String data = jsonObject.toString();
            String result;

            HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
            httpcon.setDoOutput(true);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("GET");
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
            String categorylist = response.getString("categories");
            String[] categoryList = categorylist.split(", ");


            Collections.addAll(categories, categoryList);


            httpcon.disconnect();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        Collections.sort(categories);
        return categories;
    }

    public List<String> getLocations(){

        String url = "http://www.frrndlease.com/dbctv1/service/GetLocations";
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

        Collections.sort(pincodes);

        return pincodes;
    }

    public JSONObject getLocationVector(String pincode){
        JSONObject jsonObject = new JSONObject();
        JSONObject locationVector = new JSONObject();
        int returnCode;
        try {
            jsonObject.put("location", pincode);

            String url = "http://www.frrndlease.com/dbctv1/service/GetLocationVector";
            String data = jsonObject.toString();
            String result;
//
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
            locationVector = new JSONObject(response.getString("locationVector"));

            httpcon.disconnect();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return locationVector;
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public SearchBugReturnObject SearchBug(int token){
        JSONObject jsonObject = new JSONObject();
        Incident incident = new Incident();
        SearchBugReturnObject searchBugReturnObject = new SearchBugReturnObject();

        int returncode = 1;
        try {

            jsonObject.put("token", token);

            String url = "http://www.frrndlease.com/dbctv1/service/SearchBug";
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

            returncode = response.getInt("returnCode");


            if (returncode == 0) {

                incident.set_id(response.getInt("id"));
                incident.setLatitude(response.getDouble("lat"));
                incident.setPin_code(response.getString("locality"));
                incident.setCategory(response.getString("cat"));

                int returnToken = response.getInt("returntoken");

                searchBugReturnObject.setIncident(incident);
                searchBugReturnObject.setReturnToken(returnToken);
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return searchBugReturnObject;
    }

    public void populateDBfromWebService(){

        int token = 0;
        do {
            SearchBugReturnObject searchBugReturnObject = SearchBug(token);
            Incident incident = searchBugReturnObject.getIncident();

            database.addIncident(incident, context);

            token = searchBugReturnObject.getReturnToken();
        }
        while (token!=0);

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

    private class SearchBugReturnObject{
        private Incident incident;
        private int returnToken;

        public SearchBugReturnObject() {
        }

        public SearchBugReturnObject(Incident incident, int returnToken) {
            this.incident = incident;
            this.returnToken = returnToken;
        }

        public Incident getIncident() {
            return incident;
        }

        public int getReturnToken() {
            return returnToken;
        }

        public void setIncident(Incident incident) {
            this.incident = incident;
        }

        public void setReturnToken(int returnToken) {
            this.returnToken = returnToken;
        }
    }
}
