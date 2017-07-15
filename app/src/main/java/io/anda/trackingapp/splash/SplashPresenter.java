package io.anda.trackingapp.splash;

import android.content.Context;
import android.util.Log;

import io.anda.trackingapp.App;
import io.anda.trackingapp.ConfigUtil;
import io.anda.trackingapp.RestApiManager;


public class SplashPresenter {
    private static String TAG = SplashPresenter.class.getSimpleName();

    private SplashView mSplashView;

    private RestApiManager mRestApiManager;

    public SplashPresenter() {
        mRestApiManager  = App.getInstance().getRestApiManager();
    }

    public void attachView(SplashView splashView){
        this.mSplashView = splashView;
    }


    public void getConfiguration() {
       mRestApiManager.getConfiguration(new RestApiManager.ConfigurationCallback() {
            @Override
            public void onSuccess(ConfigModel config) {
                ConfigUtil.putMarkerThemes(App.getInstance(), config.getThemes());
                mRestApiManager.stopRequestConfig();
                mSplashView.onConfigurationSuccess();
            }

            @Override
            public void onError(String message) {
                Log.d(TAG, message);
                mSplashView.onConfigurationSuccess();
                mRestApiManager.stopRequestConfig();
            }
        });
    }
}
