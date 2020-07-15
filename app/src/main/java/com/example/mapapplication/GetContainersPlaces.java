package com.example.mapapplication;

import android.os.AsyncTask;

import com.example.mapapplication.data.Container;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetContainersPlaces extends AsyncTask<Object, String, String> {

    private String googlePlaceData, url;
    private GoogleMap mMap;
    public static ArrayList<Marker> myMarkers = new ArrayList<Marker>();

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.ReadTheUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<Container> containers = null;

        DataParser dataParser = new DataParser();
        containers = dataParser.parse(s);

        DisplayContainersPlaces(containers);
    }

    private void DisplayContainersPlaces(List<Container> containers) {
        for (int i = 0; i < containers.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();

            LatLng latLng = new LatLng(containers.get(i).position.get(0), containers.get(i).position.get(1));
            markerOptions.position(latLng);
            markerOptions.title(String.valueOf(containers.get(i).id));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            myMarkers.add(mMap.addMarker(markerOptions));

//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        }
    }
}
