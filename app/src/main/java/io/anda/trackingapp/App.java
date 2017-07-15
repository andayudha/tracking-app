package io.anda.trackingapp;

import android.app.Application;

import io.anda.trackingapp.maps.LocationManager;

/**
 * Created by anda on 7/15/2017.
 */

public class App extends Application {

    private static App mInstance;

    private RestApiManager restApiManager;
    private LocationManager locationManager;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        this.restApiManager = RestApiManager.createInstance(this);
        this.locationManager = LocationManager.createInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(restApiManager!=null) restApiManager.onStop();
        if(locationManager!=null) locationManager.onStop();
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public RestApiManager getRestApiManager() {
        return restApiManager;
    }
}
