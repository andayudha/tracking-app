package io.anda.trackingapp.splash;

import android.content.Context;
import android.util.Log;

import io.anda.trackingapp.ConfigUtil;
import io.anda.trackingapp.RestApiManager;


public class SplashPresenter {

    private SplashView mSplashView;

    private Context mContext;
    private static String TAG = SplashPresenter.class.getSimpleName();

    public SplashPresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void attachView(SplashView splashView){
        this.mSplashView = splashView;
    }


    public void getConfiguration() {
        RestApiManager.getInstance(mContext).getConfiguration(new RestApiManager.ConfigurationCallback() {
            @Override
            public void onSuccess(ConfigModel config) {
                ConfigUtil.putMarkerThemes(mContext, config.getThemes());
                RestApiManager.getInstance(mContext).stopRequestConfig();
                mSplashView.onConfigurationSuccess();
            }

            @Override
            public void onError(String message) {
                Log.d(TAG, message);
                mSplashView.onConfigurationSuccess();
                RestApiManager.getInstance(mContext).stopRequestConfig();
            }
        });
    }
}
