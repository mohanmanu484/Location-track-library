package com.mohan.location.locationtrack;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by mohan on 12/11/16.
 */

public class NetworkProvider2 implements LocationListener {


    private static final String TAG = "NetworkProvider2";


    @Override
    public void onLocationChanged(Location location) {

        double lattitude= location.getLatitude();
        double longitude= location.getLongitude();

        Log.d(TAG, "onLocationChanged: latitude"+lattitude+" longitude="+longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        Log.d(TAG, "onStatusChanged: "+s);
    }

    @Override
    public void onProviderEnabled(String s) {

        Log.d(TAG, "onProviderEnabled: "+s);
    }

    @Override
    public void onProviderDisabled(String s) {

        Log.d(TAG, "onProviderDisabled: "+s);
    }
}
