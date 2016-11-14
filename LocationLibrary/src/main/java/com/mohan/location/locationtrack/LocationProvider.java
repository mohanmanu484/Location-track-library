package com.mohan.location.locationtrack;

import android.content.Intent;

import com.mohan.location.locationtrack.pojo.LocationObj;

/**
 * Created by mohan on 12/11/16.
 */

public interface LocationProvider {

    void start();
    void stop();
    LocationObj getLastKnownLocation();
    void onTimeout();
    void setTimeOut(long timeoutInmillis);
    void setLocationUpdateListener(LocationUpdateListener locationUpdateListener);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void addLocationSettings(LocationSettings locationSettings);
    void setCurrentLocationUpdate(boolean currentLocation);
    boolean isSingleLocationUpdate();
    LocationSettings getLocationSetings();
    long getTimeout();
}
