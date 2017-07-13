package io.anda.trackingapp.splash;

public interface SplashView {
    void onConfigurationSuccess();
    void onConfigurationError(String error);
    void onPermissionGranted();
    void onPermissionFailed(String message);
}
