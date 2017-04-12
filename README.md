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
    new LocationTrack.Builder(this).withProvider(locationProvider).build().getLocationUpdates(new LocationUpdateListener() {
                                                                                                            @Override
                                                                                                            public void onLocationUpdate(Location location) {
                                                                                            
                                                                                                
                                                                                                            @Override
                                                                                                            public void onTimeout() {
                                                                                                
                                                                                                            }
                                                                                                        });

# Customizing the locaton request
     
 Example distance=20;interval=3000 ;priority=HIGH 
     
     
    locationProvider.addLocationSettings(new LocationSettings.Builder().withDistance(distance).withInterval(interval).withPriority(priority).build());
    new LocationTrack.Builder(this).withProvider(locationProvider).build().getLocationUpdates(new LocationUpdateListener() {
                                                                                                            @Override
                                                                                                            public void onLocationUpdate(Location location) {
                                                                                            
                                                                                                
                                                                                                            @Override
                                                                                                            public void onTimeout() {
                                                                                                
                                                                                                            }
                                                                                                        });


# Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

    allprojects {
    		repositories {
    			...
    			maven { url "https://jitpack.io" }
    		}
    	}
# Step 2.  Add the dependency

   	dependencies {
   		compile 'com.github.mohanmanu484:Location-track-library:1.0'
   	}
   	
# Step 3. Add multidex option in your app class (if probliem in running app)

    android {
        compileSdkVersion 21
        buildToolsVersion "21.1.0"
    
        defaultConfig {
            ...
            // Enabling multidex support.
            multiDexEnabled true
        }
        ...
    }
    
    dependencies {
      compile 'com.android.support:multidex:1.0.1'
    }
    
add the below code in your application class

    @Override 
      protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
      } 
      
 add application name in manifest
 
     <?xml version="1.0" encoding="utf-8"?>
      <manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.example.android.myapplication">
          <application
              ...
              android:name="android.support.multidex.MultiDexApplication">
              ...
          </application>
      </manifest>