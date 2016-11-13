package com.mohan.location.locationtrack.pojo;

/**
 * Created by mohan on 13/11/16.
 */

public class LocationObj {

    private String provider;
    private double getLatitude;
    private double longitude;
    private float accuracy;
    private float speed;
    private long timestamp;
    private float bearingId;
    private double altitude;

    public LocationObj(double lattitude, double longitude) {
        this.getLatitude = lattitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return getLatitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }



    public void setLattitude(long getLatitude) {
        this.getLatitude = getLatitude;
    }



    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getBearingId() {
        return bearingId;
    }

    public void setBearingId(float bearingId) {
        this.bearingId = bearingId;
    }
}
