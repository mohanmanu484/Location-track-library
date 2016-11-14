package com.mohan.location.locationtrack;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mohan.location.locationtrack.pojo.LocationObj;
import com.mohan.location.locationtrack.providers.FusedLocationProvider;
import com.mohan.location.locationtrack.providers.NetworkProvider;

import static com.google.ads.AdRequest.LOGTAG;

/**
 * Created by mohan on 12/11/16.
 */

public class LocationTrack {

    private Context context;
    private LocationProvider provider;

    private LocationTrack(Builder builder) {

        this.context = builder.context;
        this.provider = builder.provider;
        if (provider instanceof FusedLocationProvider) {
            if (!checkGooglePlayServicesAvailable(context)) {
                LocationSettings locationSettings = provider.getLocationSetings();
                boolean singleLocation = provider.isSingleLocationUpdate();
                long timeout = provider.getTimeout();
                provider = new NetworkProvider(context);
                provider.addLocationSettings(locationSettings);
                provider.setCurrentLocationUpdate(singleLocation);
                provider.setTimeOut(timeout);
            }
        }
        provider.start();
    }

    private boolean checkGooglePlayServicesAvailable(Context context) {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }

        Log.e(LOGTAG, "Google Play Services not available: " + GooglePlayServicesUtil.getErrorString(status));

        return false;
    }

    public LocationTrack getLocationUpdates(LocationUpdateListener locationUpdateListener) {
        provider.setLocationUpdateListener(locationUpdateListener);
        return this;
    }

    public LocationProvider getProvider() {
        return provider;
    }

    public void stopLocationUpdates() {
        provider.stop();
    }

    public LocationObj getLastKnownLocation() {
        return provider.getLastKnownLocation();
    }

    public void getCurrentLocation(LocationUpdateListener locationUpdateListener) {
        provider.setLocationUpdateListener(locationUpdateListener);
    }


    public static class Builder {


        private Context context;
        private LocationProvider provider;


        /**
         * Constructor
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("context cannotbe null");
            }
            this.context = context;
        }

        public LocationTrack build() {
            if (provider == null) {
                throw new IllegalArgumentException("Provider canot be null");
            }
            return new LocationTrack(this);
        }

        public Builder withProvider(LocationProvider provider) {
            this.provider = provider;
            return this;
        }

    }
}
