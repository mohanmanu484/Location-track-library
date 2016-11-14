package com.mohan.location.locationtrack.providers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mohan.location.locationtrack.LocationProvider;
import com.mohan.location.locationtrack.LocationSettings;
import com.mohan.location.locationtrack.LocationUpdateListener;
import com.mohan.location.locationtrack.Priority;
import com.mohan.location.locationtrack.pojo.LocationObj;
import com.mohan.location.locationtrack.utils.LocationPref;

/**
 * Created by mohan on 12/11/16.
 */

public class NetworkProvider implements LocationListener,LocationProvider {
    private final LocationManager locationManager;
    private boolean mIsLocationFound;
    private LocationUpdateListener locationUpdateListener;
    private Context context;
    private static final String TAG = "NetworkProvider";

    private boolean currentLocation=false;
    private LocationSettings locationSettings;

    public static final int RESOLUTION_REQUIRED=100;
    public static final int RESULT_OK=-1;
    private long timeout=1000*10;
    private boolean isStopped;

    public NetworkProvider(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        useDefaultLocationSetting();
        Log.d(TAG, "NetworkProvider: ");
    }

    private void useDefaultLocationSetting() {
        locationSettings=LocationSettings.DEFAULT_SETTING;
    }

    @Override
    public void addLocationSettings(LocationSettings locationSettings){
        this.locationSettings=locationSettings;
    }

    @Override
    public boolean isSingleLocationUpdate(){

        Log.d(TAG, "isSingleLocationUpdate: ");return currentLocation;
    }

    @Override
    public LocationSettings getLocationSetings() {
        return locationSettings;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setCurrentLocationUpdate(boolean currentLocation){
        this.currentLocation=currentLocation;
        locationSettings=LocationSettings.CURRENT_LOCATION_SETTING;
        Log.d(TAG, "setCurrentLocationUpdate: ");
    }

    @Override
    public void onLocationChanged(Location location) {

        mIsLocationFound=true;
        if(locationUpdateListener!=null){
            locationUpdateListener.onLocationUpdate(location);
        }
        Log.d(TAG, "onLocationChanged: ");
        LocationPref.getInstance(context).saveLocationObj(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        Log.d(TAG, "onStatusChanged: ");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "onProviderEnabled: ");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "onProviderDisabled: ");
    }

    @Override
    public void start() {
        Log.d(TAG, "start: ");
       startLocationUpdates();
    }

    protected void startLocationUpdates() {
        Criteria criteria = getCriteria(locationSettings);
        Log.d(TAG, "startLocationUpdates: ");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the
            // missing permissions, and then overriding onRequestPermissionsResult
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        if (currentLocation) {

            locationManager.requestSingleUpdate(criteria, this, Looper.getMainLooper());
        } else {
            locationManager.requestLocationUpdates(
                    locationSettings.getInterval(), locationSettings.getDistance(), criteria, this, Looper.getMainLooper());
        }

        isStopped=false;
        mIsLocationFound=false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isStopped && !mIsLocationFound){
                    if(locationUpdateListener!=null){
                        locationUpdateListener.onTimeout();
                    }
                }

            }
        },timeout);
    }
    private Criteria getCriteria(LocationSettings locationSettings) {

        Criteria criteria=new Criteria();
        Priority priority=locationSettings.getPriority();

        Log.d(TAG, "getCriteria: "+priority);


            switch (priority) {
                case HIGH:
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setPowerRequirement(Criteria.POWER_HIGH);
                    break;
                case MEDIUM:
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
                    criteria.setBearingAccuracy(Criteria.ACCURACY_MEDIUM);
                    criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
                    criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                    break;
                case LOW:

                default:
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
                    criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
                    criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
                    criteria.setPowerRequirement(Criteria.POWER_LOW);
            }
            return criteria;
        }

    @Override
    public void stop() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
        Log.d(TAG, "stop: location update removed");
        isStopped=true;

    }


    @Override
    public LocationObj getLastKnownLocation() {

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "getLastKnownLocation: ");
            if (location != null) {
                LocationObj   locationObj=new LocationObj(location.getLatitude(),location.getLongitude());
                return locationObj;
            }
        }

        LocationObj locationObj= LocationPref.getInstance(context).getLocation();
        if(locationObj!=null){
            return locationObj;
        }
        return null;
    }

    @Override
    public void onTimeout() {
        Log.d(TAG, "onTimeout: ");
    }

    @Override
    public void setTimeOut(long timeoutInmillis) {
        Log.d(TAG, "setTimeOut: "+timeoutInmillis);
        this.timeout= timeoutInmillis;
    }

    @Override
    public void setLocationUpdateListener(LocationUpdateListener locationUpdateListener) {

        this.locationUpdateListener=locationUpdateListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==RESOLUTION_REQUIRED && resultCode==RESULT_OK){
            startLocationUpdates();
        }
    }
}
