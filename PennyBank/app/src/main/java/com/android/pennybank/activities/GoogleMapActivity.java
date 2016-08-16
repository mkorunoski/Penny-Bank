package com.android.pennybank.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.android.pennybank.R;

import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.pennybank.util.Logger;
import com.android.pennybank.web.GooglePlacesReadTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


public class GoogleMapActivity
        extends
        AppCompatActivity
        implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap = null;
    private Criteria mCriteria;
    private String mProvider;
    private LocationManager mLocationManager;
    private Location mLocation;

    private long MINUTE = 60 * 1000;
    private int PROXIMITY_RADIUS = 8000;
    private String TYPES = "";
    private static final String GOOGLE_BROWSER_KEY = "AIzaSyAC3UCVuVNPxgCBRAqL3WGsqbLoBcmyRSI";
    private static final String GOOGLE_API_KEY = "AIzaSyBGJu6QvwyIQL35YOGEaJlagOTvP_1sUwA";

    private EditText mType;
    private Button mSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        setup();

        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mCriteria = new Criteria();
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        mCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        mCriteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        mLocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
    }

    private void setup() {
        mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = (EditText) findViewById(R.id.type);
                String type = mType.getText().toString();
                if (type.equals("")) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(GoogleMapActivity.this).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Please fill required fields!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            mType.setFocusable(true);
                        }
                    });
                    alertDialog.show();
                } else {
                    TYPES = type;
                    findNearest();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }

    public void findNearest() {
        LatLng currentPosition = new LatLng(41.995929, 21.431471);
        if (mLocation != null) {
            currentPosition = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + currentPosition.latitude + "," + currentPosition.longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        if (Logger.ENABLED) {
            Log.i(Logger.TAG, TYPES);
        }
        googlePlacesUrl.append("&types=" + TYPES);
        googlePlacesUrl.append("&key=" + GOOGLE_BROWSER_KEY);

        Object[] toPass = new Object[2];
        toPass[0] = mMap;
        toPass[1] = googlePlacesUrl.toString();
        (new GooglePlacesReadTask()).execute(toPass);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mProvider = mLocationManager.getBestProvider(mCriteria, true);
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        mLocationManager.requestLocationUpdates(mProvider, MINUTE, 0, this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
