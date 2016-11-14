# Location-track-library
this is a light weight library for getting location updates

# Getting last location
 
    LocationProvider locationProvider=new FusedLocationProvider(this);
 
 or
 
    LocationProvider locationProvider=new NetworkProvider(this);
 
 you can use any of the above providers , if googleplay service is not present then it uses LocationManager by default.
 
            
    LocationObj locationObj=new LocationTrack.Builder(this).withProvider(locationProvider).build().getLastKnownLocation();

# Getting  location updates

    new LocationTrack.Builder(this).withProvider(locationProvider).build().getLastKnownLocation(new LocationUpdateListener() {
                                                                                                        @Override
                                                                                                        public void onLocationUpdate(Location location) {
                                                                                        
                                                                                            
                                                                                                        @Override
                                                                                                        public void onTimeout() {
                                                                                            
                                                                                                        }
                                                                                                    });
                                                                                                   
                                                                                                    
# Getting current location

    locationProvider.setCurrentLocationUpdate(true);
    new LocationTrack.Builder(this).withProvider(locationProvider).build().getLastKnownLocation(new LocationUpdateListener() {
                                                                                                            @Override
                                                                                                            public void onLocationUpdate(Location location) {
                                                                                            
                                                                                                
                                                                                                            @Override
                                                                                                            public void onTimeout() {
                                                                                                
                                                                                                            }
                                                                                                        });

# Customizing the locaton request
     
 Example distance=20;interval=3000 ;priority=HIGH 
     
     
    locationProvider.addLocationSettings(new LocationSettings.Builder().withDistance(distance).withInterval(interval).withPriority(priority).build());
    new LocationTrack.Builder(this).withProvider(locationProvider).build().getLastKnownLocation(new LocationUpdateListener() {
                                                                                                            @Override
                                                                                                            public void onLocationUpdate(Location location) {
                                                                                            
                                                                                                
                                                                                                            @Override
                                                                                                            public void onTimeout() {
                                                                                                
                                                                                                            }
                                                                                                        });

   