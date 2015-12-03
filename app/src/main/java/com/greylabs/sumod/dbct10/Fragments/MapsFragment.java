package com.greylabs.sumod.dbct10.Fragments;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.greylabs.sumod.dbct10.Adapters.DBHandler;
import com.greylabs.sumod.dbct10.GPSTracker;
import com.greylabs.sumod.dbct10.Model.Incident;
import com.greylabs.sumod.dbct10.R;
import com.greylabs.sumod.dbct10.WebService;

import java.util.ArrayList;

/**
 * Created by Sumod on 03-Dec-15.
 */
public class MapsFragment extends Fragment implements LocationListener {

    GoogleMap googleMap;
    GPSTracker gps = new GPSTracker(getActivity());
    private static final String TAG = "MapsFragment";
    private WebService webService;
    private DBHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        db = new DBHandler(getActivity(), null, null, 1);

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        try{
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        overlayIncidentsOnMap();
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView locationTv = (TextView) getActivity().findViewById(R.id.latlongLocation);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
    }


    public void overlayIncidentsOnMap(){
        ArrayList<Incident> incidents = db.getAllIncidentsByList();

        for (int i = 0; i<incidents.size(); i++){
            addMarker(new LatLng(incidents.get(i).getLatitude(), incidents.get(i).getLongitude()));
            Log.i(TAG, String.valueOf(incidents.get(i).getLatitude()) + "|" + String.valueOf(incidents.get(i).getLongitude()));
        }
    }

    private void addMarker(LatLng latLng){

        Log.i(TAG, String.valueOf(latLng.latitude) + "|" + String.valueOf(latLng.longitude));

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.greendot);

        /** Make sure that the map has been initialised **/
        if(null != googleMap){
            googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marker")
                            .draggable(true)
                            .icon(icon)
            );
        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

}
