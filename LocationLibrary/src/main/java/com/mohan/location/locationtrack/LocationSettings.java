package com.mohan.location.locationtrack;

/**
 * Created by mohan on 12/11/16.
 */

public class LocationSettings {

    private final long interval;
    private final float distance;
    private final Priority priority;

    public static LocationSettings DEFAULT_SETTING=new Builder().withDistance(20).withInterval(5000).withPriority(Priority.MEDIUM).build();

    public static LocationSettings CURRENT_LOCATION_SETTING=new Builder().withDistance(0).withInterval(1000).withPriority(Priority.HIGH).build();



    private LocationSettings(Builder builder) {
        this.interval = builder.interval;
        this.distance = builder.distance;
        this.priority = builder.priority;
    }

    public long getInterval() {
        return interval;
    }

    public float getDistance() {
        return distance;
    }

    public Priority getPriority() {
        return priority;
    }

    public static class Builder {

        private long interval;
        private float distance;
        private Priority priority;

        public LocationSettings build() {
            return new LocationSettings(this);
        }

        public Builder withDistance(float distance) {
            this.distance = distance;
            return this;
        }

        public Builder withInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder withPriority(Priority priority) {
            this.priority = priority;
            return this;
        }

    }
}
