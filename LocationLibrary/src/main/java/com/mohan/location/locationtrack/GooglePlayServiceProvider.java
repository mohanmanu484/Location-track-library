package com.mohan.location.locationtrack;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

/**
 * Created by mohan on 11/11/16.
 */

public class GooglePlayServiceProvider implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private Context context;
    private boolean isConnected;

    private static final String TAG = "GooglePlayServiceProvider";

    public GooglePlayServiceProvider() {
    }

    public void stopLocationUpdates() {
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected() ) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(TAG, "stopLocationUpdates: ");
            mGoogleApiClient.disconnect();
        }
    }

    public GooglePlayServiceProvider(Context context) {
        this.context = context;
    }

    public void with(Context context){
        this.context=context;
    }

    public void start(){
        Log.d(TAG, "start: "+isConnected);
        if(!isConnected) {
            mGoogleApiClient = GoogleApiClientBuilder.getInstance(context,this,this);
            mGoogleApiClient.connect();
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

       // checkLocationSettings();

        if(mGoogleApiClient.isConnected()) {
            Log.d(TAG, "startLocationUpdates: "+"request location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }else {
            Log.d(TAG, "startLocationUpdates: not connected");
        }
    }


    @Override
    public void onLocationChanged(Location location) {
       double lattitude= location.getLatitude();
       double longitude= location.getLongitude();

        Log.d(TAG, "onLocationChanged: latitude"+lattitude+" longitude="+longitude);

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        startLocationUpdates(createLocationRequest());
        isConnected=true;

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "onConnectionSuspended: ");
        isConnected=false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed: ");
        isConnected=false;
    }

    public void stop() {
        stopLocationUpdates();
    }

    private void checkLocationSettings() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().setAlwaysShow(true).addLocationRequest(createLocationRequest()).build();
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, request).setResultCallback(settingsResultCallback);
    }

    private ResultCallback<LocationSettingsResult> settingsResultCallback = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult locationSettingsResult) {
            final Status status = locationSettingsResult.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.d(TAG, "onResult: suceess");
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
