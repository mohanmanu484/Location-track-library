package com.mohan.location.locationtrack.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.mohan.location.locationtrack.pojo.LocationObj;

/**
 * Created by mohan on 13/11/16.
 */

public class LocationPref {

    public static final String LOCATION_PREF = "location_pref";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    public static final String LATTITUDE = "lattitude";
    public static final String LONGITUDE = "londgitude";

    private static LocationPref instance;
    private static final String TAG = "LocationPref";

    /**
     * The instance gets created only when it is called for first time. Lazy-loading
     */
    public static synchronized LocationPref getInstance(Context context) {

        if (instance == null) {
            instance = new LocationPref();
            createSharedPref(context);
        }

        return instance;
    }


    private static void createSharedPref(Context context) {
        Log.d(TAG, "createSharedPref: ");
        preferences = context.getSharedPreferences(LOCATION_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void saveLocationObj(Location location) {
        if (location == null) {
            return;
        }
        editor.putLong(LATTITUDE, Double.doubleToLongBits(location.getLatitude()));
        editor.putLong(LONGITUDE, Double.doubleToLongBits(location.getLongitude()));
        editor.apply();
        editor.commit();
        Log.d(TAG, "saveLocationObj: ");
    }

    public  LocationObj getLocation(){
        LocationObj locationObj=new LocationObj(Double.longBitsToDouble(preferences.getLong(LATTITUDE,0)),Double.longBitsToDouble(preferences.getLong(LONGITUDE,0)));
        Log.d(TAG, "getLocation: "+locationObj.getLatitude()+" "+locationObj.getLongitude());
        return locationObj;
    }
}
