package com.mohan.location.locationtracklibrary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mohan.location.locationtrack.FusedLocationProvider;
import com.mohan.location.locationtrack.LocationProvider;
import com.mohan.location.locationtrack.LocationSettings;
import com.mohan.location.locationtrack.LocationTrack;
import com.mohan.location.locationtrack.LocationUpdateListener;
import com.mohan.location.locationtrack.Priority;
import com.mohan.location.locationtrack.pojo.LocationObj;

import static com.mohan.location.locationtracklibrary.R.id.interval;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, LocationUpdateListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 100;

    private static final String TAG = "MainActivity";
    EditText etInterval;
    EditText etDistance;
    TextView lastLocation;
    TextView updatedLocation;
    Spinner prioritySpinner;
    CheckBox defaultCb;
    Priority priority;
    private FusedLocationProvider locationProvider;
    private LocationTrack locationTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etInterval = (EditText) findViewById(interval);
        etDistance = (EditText) findViewById(R.id.distance);
        lastLocation = (TextView) findViewById(R.id.lastLocationText);
        updatedLocation = (TextView) findViewById(R.id.updtedLocationText);
        prioritySpinner = (Spinner) findViewById(R.id.proritySpinner);
        defaultCb = (CheckBox) findViewById(R.id.checkBox);
        defaultCb.setOnCheckedChangeListener(this);
        defaultCb.setChecked(true);
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    priority = Priority.LOW;
                } else if (position == 1) {
                    priority = Priority.MEDIUM;
                } else {
                    priority = Priority.HIGH;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void getCurrentLocation(View view) {
        locationProvider = new FusedLocationProvider(this);
        locationProvider.setCurrentLocationUpdate(true);
        locationProvider.setTimeOut(1000 * 10);
        checkForPermission();
    }


    public void requestLocationUpdates(View view) {

        locationProvider = new FusedLocationProvider(this);
        if (!defaultCb.isChecked()) {
            if (etInterval.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter interval", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etDistance.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter distance", Toast.LENGTH_SHORT).show();
                return;
            }

            long interval = Long.valueOf(etInterval.getText().toString());
            float distance = Float.valueOf(etDistance.getText().toString());


            locationProvider.setTimeOut(1000 * 10);
            locationProvider.addLocationSettings(new LocationSettings.Builder().withDistance(distance).withInterval(interval).withPriority(priority).build());
        }
        checkForPermission();
    }

    private void requestLocationUpdates(LocationProvider locationProvider){
        locationTrack = new LocationTrack.Builder(this).withProvider(locationProvider).build().createLocationUpdates(this);
    }

    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d(TAG, "requestLocationUpdates: " + "Show an expanation to the user");

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            requestLocationUpdates(locationProvider);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "onRequestPermissionsResult: " + "permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    requestLocationUpdates(locationProvider);

                } else {

                    Log.d(TAG, "onRequestPermissionsResult: " + "permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (locationProvider != null) {
            locationProvider.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void stopUpdates(View view) {
        if (locationTrack != null) {

            locationTrack.stopLocationUpdates();
        }
    }

    public void lastLocation(View view) {
        if(locationTrack==null){
            requestLocationUpdates(new FusedLocationProvider(this));
        }
        LocationObj location = locationTrack.getLastKnownLocation();
        ;
        if (location != null) {
            lastLocation.setText("Last location: " + location.getLongitude() + " " + location.getLatitude());
            Log.d(TAG, "lastLocation: " + location.getLongitude() + " " + location.getLatitude());
        } else {
            Log.d(TAG, "lastLocation: location is null");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            etDistance.setEnabled(false);
            etInterval.setEnabled(false);
            prioritySpinner.setEnabled(false);
        } else {
            etDistance.setEnabled(true);
            etInterval.setEnabled(true);
            prioritySpinner.setEnabled(true);
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        updatedLocation.setText("updated location: " + location.getLongitude() + " " + location.getLatitude());
        Log.d(TAG, "onLocationUpdate: lattitude=" + location.getLatitude() + " longitude=" + location.getLongitude());
        /*if (((FusedLocationProvider)locationTrack.getProvider()).isSingleLocationUpdate()) {
            locationTrack.stopLocationUpdates();
        }*/
    }

    @Override
    public void onTimeout() {
        Toast.makeText(MainActivity.this, "Timed out , location request stopped", Toast.LENGTH_SHORT).show();
        locationTrack.stopLocationUpdates();
    }
}
