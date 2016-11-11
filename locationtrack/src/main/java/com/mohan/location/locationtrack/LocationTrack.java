package com.mohan.location.locationtrack;

import android.content.Context;

/**
 * Created by mohan on 12/11/16.
 */

public class LocationTrack {

    private Context context;
    private GooglePlayServiceProvider provider;

    private LocationTrack(Builder builder) {

        this.context=builder.context;
        this.provider=builder.provider;
    }

    public void createLocationUpdates(){
        provider.start();
    }

    public void stopLocationUpdates(){
        provider.stop();
    }




    public static class Builder {


        private Context context;
        private GooglePlayServiceProvider provider;


        /**
         * Constructor
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("profession and name can not be null");
            }
            this.context = context;
            this.provider=new GooglePlayServiceProvider(context);
        }

        public LocationTrack build() {
            return new LocationTrack(this);
        }

        public Builder withProvider(GooglePlayServiceProvider provider){
            provider.with(context);
            this.provider=provider;
            return this;
        }
    }
}
