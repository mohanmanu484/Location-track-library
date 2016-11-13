package com.mohan.location.locationtrack;

import android.location.Location;

/**
 * Created by mohan on 12/11/16.
 */

public interface LocationUpdateListener {

    void onLocationUpdate(Location location);
    void onTimeout();
}
