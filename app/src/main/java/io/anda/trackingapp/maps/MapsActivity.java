package io.anda.trackingapp.maps;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.anda.trackingapp.ConfigUtil;
import io.anda.trackingapp.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, MapsView {

    private static final int LOCATION_SETTINGS_CODE = 996;

    private GoogleMap mMap;
    private MapsPresenter mapsPresenter;
    private SupportMapFragment mapFragment;
    private Marker marker;
    private TextView balanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapsPresenter = new MapsPresenter(this);
        mapsPresenter.checkLocationSettings();

        setContentView(R.layout.activity_main);
        balanceTextView = (TextView) findViewById(R.id.balance);
        int balance = ConfigUtil.getBalanceConfig(this);
        balanceTextView.setText(getString(R.string.balance)+" "+balance);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapsPresenter.attachView(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(-6.2707741, 106.9542327);

        int themes = ConfigUtil.getMarkerThemeDrawable(this);
        marker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(themes))
                .position(defaultLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, LocationConfig.CAMERA_ZOOM));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationUpdate(Location location) {
        moveToCurrentLocation(location);
    }

    private void moveToCurrentLocation(Location currentLocation){
        animateMarker(marker, currentLocation, false);
        float bearing = currentLocation.getBearing();
        marker.setRotation(bearing);
        marker.setFlat(true);
    }

    public void animateMarker(final Marker marker, final Location location,
                              final boolean hideMarker) {
        final LatLng toPosition = new LatLng(location.getLatitude(), location.getLongitude());
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });

    }

    @Override
    public void onCameraMapsUpdate(final LatLng latLng) {
        if(mMap==null || latLng==null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, LocationConfig.CAMERA_ZOOM));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(LocationConfig.CAMERA_ZOOM), LocationConfig.CAMERA_ANIMATION_DURATION, null);
            }
        });


    }

    @Override
    public void onLocationSettingsRequired(Status status) {
        try {
            status.startResolutionForResult(this, LOCATION_SETTINGS_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case LOCATION_SETTINGS_CODE :
                if(resultCode==RESULT_OK){
                    mapsPresenter.disableLocationSetting();
                }
        }
    }

    @Override
    public void onRequestLocationFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapsPresenter.onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapsPresenter.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
