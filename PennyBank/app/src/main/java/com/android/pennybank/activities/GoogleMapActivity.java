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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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

    private GoogleMap mGoogleMap = null;
    private Criteria mCriteria;
    private String mProvider;
    private LocationManager mLocationManager;
    private Location mLocation;

    private long MINUTE = 60 * 1000;
    private int PROXIMITY_RADIUS = 8000;
    private String TYPE = "";
    private static final String GOOGLE_BROWSER_KEY = "AIzaSyAC3UCVuVNPxgCBRAqL3WGsqbLoBcmyRSI";

    private AutoCompleteTextView mType;
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

    private static final String[] TYPES = new String[]{
            "Airport",
            "Amusement park",
            "Aquarium",
            "Art gallery",
            "Beauty salon",
            "Bicycle store",
            "Book store",
            "Car dealer",
            "Car rental",
            "Car repair",
            "Clothing store",
            "Dentist",
            "Department store",
            "Doctor",
            "Electrician",
            "Electronics store",
            "Florist",
            "Furniture store",
            "Hair care",
            "Hardware store",
            "Hindu temple",
            "Home goods store",
            "Hospital",
            "Insurance agency",
            "Jewelry store",
            "Laundry",
            "Lodging",
            "Movie theater",
            "Museum",
            "Night club",
            "Pet store",
            "Physiotherapist",
            "Real estate agency",
            "School",
            "Shoe store",
            "Shopping mall",
            "Spa",
            "Stadium",
            "Store",
            "Train station",
            "Transit station",
            "Travel agency",
            "University",
            "Veterinary care"
    };

    private void setup() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, TYPES);
        mType = (AutoCompleteTextView) findViewById(R.id.type);
        mType.setAdapter(adapter);
        mSearch = (Button) findViewById(R.id.search);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    type = type.replaceAll(" ", "_").toLowerCase();
                    TYPE = type;
                    findNearest();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
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
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + currentPosition.latitude + "," + currentPosition.longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        if (Logger.ENABLED) {
            Log.i(Logger.TAG, TYPE);
        }
        googlePlacesUrl.append("&types=" + TYPE);
        googlePlacesUrl.append("&key=" + GOOGLE_BROWSER_KEY);

        Object[] toPass = new Object[2];
        toPass[0] = mGoogleMap;
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
