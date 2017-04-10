package com.mohan.location.locationtracklibrary;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mohan.location.locationtrack.LocationSettings;
import com.mohan.location.locationtrack.LocationTrack;
import com.mohan.location.locationtrack.Priority;
import com.mohan.location.locationtrack.providers.FusedLocationProvider;

import rx.functions.Action1;

public class RxLocationActivity extends AppCompatActivity {

    public static final String TAG="RxLocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_location);

        FusedLocationProvider fusedLocationProvider=new FusedLocationProvider(this);
        fusedLocationProvider.addLocationSettings(new LocationSettings.Builder().withDistance(0).withInterval(1).withPriority(Priority.HIGH).build());

        new LocationTrack.Builder(this).withProvider(fusedLocationProvider).build().getLocationUpdates().subscribe(new Action1<Location>() {
            @Override
            public void call(Location location) {

                Log.d(TAG, "call: "+location.getLongitude());

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });;


    }
}
