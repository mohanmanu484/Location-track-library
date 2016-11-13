package com.mohan.location.locationtrack;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.mohan.location.locationtrack.pojo.LocationObj;

/**
 * Created by mohan on 12/11/16.
 */

public class NetworkProvider implements LocationListener,LocationProvider {
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    @Override
    public LocationObj getLastKnownLocation() {

        return null;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public void setTimeOut(long timeoutInmillis) {

    }

    @Override
    public void setLocationUpdateListener(LocationUpdateListener locationUpdateListener) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


    }
}
