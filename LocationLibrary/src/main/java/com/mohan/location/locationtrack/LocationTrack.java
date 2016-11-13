package com.mohan.location.locationtrack;

import android.content.Context;

import com.mohan.location.locationtrack.pojo.LocationObj;

/**
 * Created by mohan on 12/11/16.
 */

public class LocationTrack {

    private Context context;
    private LocationProvider provider;

    private LocationTrack(Builder builder) {

        this.context = builder.context;
        this.provider = builder.provider;
        provider.start();
    }

    public LocationTrack createLocationUpdates(LocationUpdateListener locationUpdateListener) {
        provider.setLocationUpdateListener(locationUpdateListener);
        return this;
    }

    public LocationProvider getProvider(){
        return provider;
    }

    public void stopLocationUpdates() {
        provider.stop();
    }

    public LocationObj getLastKnownLocation() {
        return provider.getLastKnownLocation();
    }

    public void getCurrentLocation(LocationUpdateListener locationUpdateListener){
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
