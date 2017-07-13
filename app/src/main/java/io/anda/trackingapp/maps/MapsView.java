package io.anda.trackingapp.maps;

import android.location.Location;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

public interface MapsView {
    void onLocationUpdate(Location location);
    void onRequestLocationFailed(String message);
    void onLocationSettingsRequired(Status status);
    void onCameraMapsUpdate(LatLng latLng);
}
