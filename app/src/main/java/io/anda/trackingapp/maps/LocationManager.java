package io.anda.trackingapp.maps;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscriber;
import rx.Subscription;

public class LocationManager {

    private static  final String TAG = LocationManager.class.getSimpleName();

    private static LocationManager instance = null;

    private Context mContext;

    private Subscription locationSubscription;
    private Subscription resolutionSettingSubscription;

    private Timer cameraTimer;

    private ReactiveLocationProvider locationProvider;
    private LocationRequest request;

    private LatLng mlLatLng;

    public LocationManager(Context context) {
        this.mContext = context;
    }

    public static LocationManager getInstance(Context context) {
        if(instance==null) instance = new LocationManager(context);
        return instance;
    }

    public void initLocationService() {
        request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LocationConfig.LOCATION_UPDATE_INTERVALS);

        locationProvider = new ReactiveLocationProvider(mContext);
    }

    public void requestUpdateLocation(final RequestLocationCallback callback){
        locationSubscription = locationProvider.getUpdatedLocation(request)
                .subscribe(new Subscriber<Location>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        callback.onUpdateError(e.getMessage());
                    }

                    @Override
                    public void onNext(Location location) {
                        Log.d(TAG, "location updated "+location.getLatitude()+", "+location.getLongitude());
                        callback.onLocationUpdated(location);
                        mlLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                });
    }

    public void checkLocationSettings(final LocationSettingsCallback callback) {
        resolutionSettingSubscription = locationProvider.checkLocationSettings(new LocationSettingsRequest.Builder()
                .addLocationRequest(request)
                .setAlwaysShow(true)
                .build())
                .subscribe(new Subscriber<LocationSettingsResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result.getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                Log.d(TAG, "LocationSettingsStatusCodes.SUCCESS");
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.d(TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                                callback.onLocationSettingsRequired(status);
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.d(TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                                break;
                        }
                    }
                });
    }

    public void initCameraTimer(final CameraIntervalCallback callback){
        cameraTimer = new Timer();
        cameraTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                callback.onIntervalCalled(mlLatLng);
            }
        }, 0, LocationConfig.CAMERA_INTERVALS);
    }

    public void onStop(){
        if(resolutionSettingSubscription!=null) resolutionSettingSubscription.unsubscribe();
        if(locationSubscription!=null) locationSubscription.unsubscribe();
        if(cameraTimer!=null) cameraTimer.cancel();
    }

    public void disableLocationSetting(){
        if(resolutionSettingSubscription!=null) resolutionSettingSubscription.unsubscribe();
    }

    public interface LocationSettingsCallback {
        void onLocationSettingsRequired(Status status);
    }

    public interface RequestLocationCallback{
        void onLocationUpdated(Location location);
        void onUpdateError(String message);
    }

    public interface CameraIntervalCallback{
        void onIntervalCalled(LatLng latLng);
    }
}
