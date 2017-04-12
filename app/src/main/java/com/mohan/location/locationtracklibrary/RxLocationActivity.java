package com.mohan.location.locationtracklibrary;

import android.location.Location;
import android.os.Bundle;
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
    Observable<Location> locationObservable;
    Subscription subscription;

    EditText interval;
    EditText distance;
    TextView lattitude,longitude;
    Button start,stop;

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

        long intervalValue=Long.parseLong(distance.getText().toString());
        float distancValue=Float.parseFloat(distance.getText().toString());


        FusedLocationProvider fusedLocationProvider=new FusedLocationProvider(this);
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
}