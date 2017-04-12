package com.mohan.location.locationtrack.providers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mohan.location.locationtrack.GoogleApiClientBuilder;
import com.mohan.location.locationtrack.LocationProvider;
import com.mohan.location.locationtrack.LocationSettings;
import com.mohan.location.locationtrack.LocationUpdateListener;
import com.mohan.location.locationtrack.pojo.LocationObj;
import com.mohan.location.locationtrack.utils.LocationPref;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by mohan on 12/11/16.
 */

public class FusedLocationProvider implements LocationListener, LocationProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "FusedLocationProvider";
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private static boolean isConnected = false;
    private LocationSettings locationSettings;
    private LocationRequest locationRequest;
    private boolean currentLocation = false;
    private boolean checkGpsEnabled = true;
    private LocationUpdateListener locationUpdateListener;

    Observable<Location> locationObserver;

    public static final int RESOLUTION_REQUIRED = 100;
    public static final int RESULT_OK = -1;
    public static final int RESULT_CANCELLED = 0;
    private boolean isStopped = true;
    private boolean mIsLocationFound = false;
    private long timeout = 1000 * 10;   // ten seconds

    private static Subscriber<? super Location> locationSubscriber;

    public FusedLocationProvider(Context context) {
        this.context = context;
        useDefaultLocationSetting();
        locationObserver = Observable.create(new Observable.OnSubscribe<Location>() {

            @Override
            public void call(Subscriber<? super Location> subscriber) {


                Log.d(TAG, "call: "+subscriber.isUnsubscribed());
                FusedLocationProvider.this.locationSubscriber = subscriber;
            }
        });
    }

    @Override
    public boolean isSingleLocationUpdate() {
        return currentLocation;
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
    public Observable<Location> getLocationUpdates() {
        return locationObserver;
    }

    @Override
    public void setCurrentLocationUpdate(boolean currentLocation) {
        this.currentLocation = currentLocation;
        locationSettings = LocationSettings.CURRENT_LOCATION_SETTING;
    }

    private void useDefaultLocationSetting() {
        locationSettings = LocationSettings.DEFAULT_SETTING;
    }

    @Override
    public void addLocationSettings(LocationSettings locationSettings) {
        this.locationSettings = locationSettings;
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged: ");

        mIsLocationFound = true;
        if (locationUpdateListener != null) {
            locationUpdateListener.onLocationUpdate(location);
        }
        LocationPref.getInstance(context).saveLocationObj(location);

        if (locationSubscriber != null) {

            if (!locationSubscriber.isUnsubscribed()) {

                locationSubscriber.onNext(location);
            }else {
                stopLocationUpdates();
            }
        }


    }





    private LocationRequest createLocationRequest(LocationSettings settings, boolean currentLocation) {
        LocationRequest request = LocationRequest.create()
                .setFastestInterval(settings.getInterval())
                .setInterval(settings.getInterval())
                .setSmallestDisplacement(settings.getDistance());
        Log.d(TAG, "createLocationRequest: " + request.getPriority());

        switch (settings.getPriority()) {
            case HIGH:
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                break;
            case MEDIUM:
                request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                break;
            case LOW:
                request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                break;

        }

        if (currentLocation) {
            request.setNumUpdates(1);
        }

        return request;
    }

    private void useNetworkProvider() {
        LocationSettings locationSettings = this.getLocationSetings();
        boolean singleLocation = this.isSingleLocationUpdate();
        long timeout = this.getTimeout();
        NetworkProvider provider = new NetworkProvider(context);
        provider.addLocationSettings(locationSettings);
        provider.setCurrentLocationUpdate(singleLocation);
        provider.setTimeOut(timeout);
        provider.start();
    }

    @Override
    public void start() {

        Log.d(TAG, "start: isConnected" + isConnected);
        locationRequest = createLocationRequest(locationSettings, currentLocation);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClientBuilder.getInstance(context, this, this);
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            startLocationUpdates(locationRequest);
        }

    }

    @Override
    public void stop() {
        stopLocationUpdates();
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(TAG, "stopLocationUpdates: ");
            mGoogleApiClient.disconnect();
            isStopped = true;
        }
        if (locationSubscriber != null) {
            locationSubscriber.onCompleted();
        }
        if (locationSubscriber != null && !locationSubscriber.isUnsubscribed()) {
            locationSubscriber.unsubscribe();
            locationSubscriber = null;
        }


    }

    @Override
    public LocationObj getLastKnownLocation() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
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
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                LocationObj locationObj = new LocationObj(location.getLatitude(), location.getLongitude());
                return locationObj;
            }
        }

        LocationObj locationObj = LocationPref.getInstance(context).getLocation();
        if (locationObj != null) {
            return locationObj;
        }
        return null;
    }

    @Override
    public void onTimeout() {

    }

    @Override
    public void setTimeOut(long timeoutInmillis) {
        this.timeout = timeoutInmillis;
    }

    @Override
    public void setLocationUpdateListener(LocationUpdateListener locationUpdateListener) {
        this.locationUpdateListener = locationUpdateListener;
    }

    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }

        // checkLocationSettings();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.d(TAG, "startLocationUpdates: " + "request location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            isStopped = false;
            mIsLocationFound = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isStopped && !mIsLocationFound) {
                        if (locationUpdateListener != null) {
                            locationUpdateListener.onTimeout();
                        }
                    }

                }
            }, timeout);
        } else {
            Log.d(TAG, "startLocationUpdates: not connected");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (checkGpsEnabled) {
            checkLocationSettings();
            return;
        }

        startLocationUpdates(locationRequest);

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "onConnectionSuspended: ");
        useNetworkProvider();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
        useNetworkProvider();
    }

    private void checkLocationSettings() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().setAlwaysShow(true).addLocationRequest(locationRequest).build();
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, request).setResultCallback(settingsResultCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESOLUTION_REQUIRED && resultCode == RESULT_OK) {
            startLocationUpdates(locationRequest);
        }
    }

    private ResultCallback<LocationSettingsResult> settingsResultCallback = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.d(TAG, "onResult: suceess");
                    startLocationUpdates(locationRequest);

                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.d(TAG, "onResult: resolution rquired");

                    if (context instanceof Activity) {
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult((Activity) context, 100);
                        } catch (IntentSender.SendIntentException e) {
                            //logger.i("PendingIntent unable to execute request.");
                            Log.d(TAG, "onResult: ");
                        }

                    } else {
                        Log.d(TAG, "onResult: not activity");
                        // logger.w("Provided context is not the context of an activity, therefore we cant launch the resolution activity.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.d(TAG, "onResult: change unavailable");
                    break;
            }
        }
    };


}
