package com.mohan.location.locationtracklibrary;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by mohan on 12/11/16.
 */

public class App extends Application{


    @Override
    public void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }


}
