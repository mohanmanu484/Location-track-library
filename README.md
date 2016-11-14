# Location-track-library
this is a light weight library for getting location updates

# Getting last location
 
 LocationProvider locationProvider=new FusedLocationProvider(this);
 
 or
 
 LocationProvider locationProvider=new NetworkProvider(this);
 
 you can use any of the above providers , if googleplay service is not present then it uses LocationManager by default.
 
            
LocationObj locationObj=new LocationTrack.Builder(this).withProvider(locationProvider).build().getLastKnownLocation();


