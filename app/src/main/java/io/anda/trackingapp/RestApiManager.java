package io.anda.trackingapp;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import io.anda.trackingapp.splash.ConfigModel;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RestApiManager {

    private static final long TIMEOUT = 10;

    private Context mContext;
    private Retrofit retrofit;
    private RestService restService;

    private Subscription configSubscription;
    private Subscription reportSubscription;

    private static RestApiManager instance = null;

    public RestApiManager(Context context) {
        this.mContext = context;
        retrofit = new Retrofit.Builder()
                .client(buildOkHttpsClient(context))
                .baseUrl(BuildConfig.SERVER_URI)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restService = retrofit.create(RestService.class);
    }

    public static RestApiManager getInstance(Context context) {
        if(instance==null) instance = new RestApiManager(context);
        return instance;
    }

    public void getConfiguration(final ConfigurationCallback callback){
        configSubscription = restService.getConfiguration(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<ConfigModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
            }

            @Override
            public void onNext(Response<ConfigModel> configurationResponse) {
                if(configurationResponse.isSuccessful()){
                    ConfigModel configuration = configurationResponse.body();
                    callback.onSuccess(configuration);
                }else{
                    callback.onError("failed get configuration from server");
                }
            }
        });
    }

    public void reportLocationToServer(ReportData data, final ReportLocationCallback callback){
        reportSubscription = restService.sendReport(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(Response response) {
                        if(response.isSuccessful()) callback.onSuccess();
                        else callback.onError("send report failed");
                    }
                });
    }

    public void stopReportingLocation(){
        if(reportSubscription!=null) reportSubscription.unsubscribe();
    }

    public void stopRequestConfig(){
        if(configSubscription !=null) configSubscription.unsubscribe();
    }

    private static OkHttpClient buildOkHttpsClient(final Context context) {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public interface ConfigurationCallback{
        void onSuccess(ConfigModel configuration);
        void onError(String message);
    }

    public interface ReportLocationCallback{
        void onSuccess();
        void onError(String message);
    }
}
