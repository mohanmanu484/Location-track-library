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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mohan.location.locationtrack.LocationSettings;
import com.mohan.location.locationtrack.LocationTrack;
import com.mohan.location.locationtrack.Priority;
import com.mohan.location.locationtrack.providers.FusedLocationProvider;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class RxLocationActivity extends AppCompatActivity {

    public static final String TAG="RxLocationActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 200;
    Observable<Location> locationObservable;
    Subscription subscription;

    EditText interval;
    EditText distance;
    TextView lattitude,longitude;
    Button start,stop;

    FusedLocationProvider fusedLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_location);
        interval= (EditText) findViewById(R.id.etInterval);
        distance= (EditText) findViewById(R.id.etDistance);
        lattitude= (TextView) findViewById(R.id.tvLattitude);
        longitude= (TextView) findViewById(R.id.tvLongitude);
        start= (Button) findViewById(R.id.btStart);
        stop= (Button) findViewById(R.id.btStop);
        start.setEnabled(true);

        interval.setText(1+"");
        distance.setText(0+"");

    }

    @Override
    protected void onStop() {
        super.onStop();
        stop(null);
    }

    public void stop(View v){

        if(subscription!=null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        start.setEnabled(true);



    }

    public void start(View view) {

        checkForPermission();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fusedLocationProvider != null) {
            fusedLocationProvider.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void startLocationUpdates(){
        long intervalValue=Long.parseLong(distance.getText().toString());
        int distancValue=Integer.parseInt(distance.getText().toString());


        fusedLocationProvider=new FusedLocationProvider(this);
        fusedLocationProvider.addLocationSettings(new LocationSettings.Builder().withDistance(distancValue).withInterval(intervalValue).withPriority(Priority.HIGH).build());

        locationObservable= new LocationTrack.Builder(this).withProvider(fusedLocationProvider).build().getLocationUpdates();
        subscription=locationObservable.subscribe(new Action1<Location>() {
            @Override
            public void call(Location location) {

                lattitude.setText("Lattitude : "+location.getLatitude()+"");
                longitude.setText("Longitude : "+location.getLongitude()+"");

                Log.d(TAG, "call: "+location.getLongitude());

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

                Log.d(TAG, "call: "+ throwable.getMessage());
            }
        });;
        start.setEnabled(false);
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
            startLocationUpdates();
           // requestLocationUpdates(locationProvider);
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
                    //requestLocationUpdates(locationProvider);
                    startLocationUpdates();

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

}