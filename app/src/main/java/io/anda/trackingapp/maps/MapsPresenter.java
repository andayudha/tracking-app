package io.anda.trackingapp.maps;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

import io.anda.trackingapp.ReportData;
import io.anda.trackingapp.RestApiManager;

public class MapsPresenter {

    private Context mContext;
    private MapsView mMapsView;
    private static final String TAG = MapsPresenter.class.getSimpleName();

    public MapsPresenter(Context context) {
        this.mContext = context;
        LocationManager.getInstance(mContext).initLocationService();
    }

    public void attachView(final MapsView mapsView) {
        this.mMapsView = mapsView;
        requestUpdateLocation();

        LocationManager.getInstance(mContext).initCameraTimer(new LocationManager.CameraIntervalCallback() {
            @Override
            public void onIntervalCalled(LatLng latLng) {
                mMapsView.onCameraMapsUpdate(latLng);
            }
        });
    }

    private void requestUpdateLocation(){
        LocationManager.getInstance(mContext).requestUpdateLocation(new LocationManager.RequestLocationCallback() {
            @Override
            public void onLocationUpdated(Location location) {
                mMapsView.onLocationUpdate(location);
                reportLocationToServer(location);
            }

            @Override
            public void onUpdateError(String message) {
                mMapsView.onRequestLocationFailed(message);
            }
        });
    }

    private void reportLocationToServer(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        RestApiManager.getInstance(mContext).reportLocationToServer(new ReportData(latitude, longitude),
                new RestApiManager.ReportLocationCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "report location success");
                    }

                    @Override
                    public void onError(String message) {
                        Log.d(TAG, message);
                    }
                });
    }

    public void checkLocationSettings(){
        LocationManager.getInstance(mContext).checkLocationSettings(new LocationManager.LocationSettingsCallback() {
            @Override
            public void onLocationSettingsRequired(Status status) {
                mMapsView.onLocationSettingsRequired(status);
            }
        });
    }

    public void onStop(){
        LocationManager.getInstance(mContext).onStop();
        RestApiManager.getInstance(mContext).stopReportingLocation();
    }

    public void disableLocationSetting(){
        LocationManager.getInstance(mContext).disableLocationSetting();
    }
}
