package com.android.pennybank.web;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    GoogleMap mGoogleMap;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {
        String googlePlacesData;
        JSONObject googlePlacesJson;
        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();
        try {
            mGoogleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);

            googlePlacesJson = new JSONObject(googlePlacesData);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");

            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(placeName));
        }
    }

}

