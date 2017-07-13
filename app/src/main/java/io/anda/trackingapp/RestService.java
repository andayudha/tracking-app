package io.anda.trackingapp;

import io.anda.trackingapp.splash.ConfigModel;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface RestService {

    @GET("/config")
    Observable<Response<ConfigModel>> getConfiguration(@Query("config") boolean config);

    @POST("/track")
    Observable<Response<ResponseBody>> sendReport(@Body ReportData data);
}
