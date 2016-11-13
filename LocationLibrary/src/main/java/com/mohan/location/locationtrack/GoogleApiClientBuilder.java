package com.mohan.location.locationtrack;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public final class GoogleApiClientBuilder {

  private static GoogleApiClient instance;

  private GoogleApiClientBuilder() {}

  /**
   * The instance gets created only when it is called for first time. Lazy-loading
   */
  public static synchronized GoogleApiClient getInstance(Context context, GoogleApiClient.OnConnectionFailedListener connectionFailedListener, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {

    if (instance == null) {
      instance = new GoogleApiClient.Builder(context)
              .addApi(LocationServices.API)
              .addConnectionCallbacks(connectionCallbacks)
              .addOnConnectionFailedListener(connectionFailedListener)
              .build();;
    }

    return instance;
  }
}