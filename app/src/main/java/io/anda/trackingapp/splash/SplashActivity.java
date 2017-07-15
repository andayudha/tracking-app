package io.anda.trackingapp.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import io.anda.trackingapp.R;
import io.anda.trackingapp.maps.MapsActivity;
import rx.Subscriber;

public class SplashActivity extends AppCompatActivity implements SplashView {

    private SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashPresenter = new SplashPresenter();
        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSplashPresenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConfigurationSuccess() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onConfigurationError(String error) {

    }

    @Override
    public void onPermissionGranted() {
      mSplashPresenter.getConfiguration();
    }

    @Override
    public void onPermissionFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void checkPermission() {
        new RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onPermissionFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(Boolean granted) {
                        if(granted){
                            onPermissionGranted();
                        }else{
                            onPermissionFailed("permission canceled");
                        }
                    }
                });

    }
}
