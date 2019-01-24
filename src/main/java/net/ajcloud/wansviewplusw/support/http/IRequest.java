package net.ajcloud.wansviewplusw.support.http;

import com.google.gson.JsonObject;
import net.ajcloud.wansviewplusw.support.http.bean.*;
import net.ajcloud.wansviewplusw.support.http.bean.device.DeviceListBean;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IRequest {
    @POST("/api/v1/app-startup")
    Call<ResponseBean<AppStartUpBean>> appStartUp(@Body JsonObject body);

    @POST("challenge")
    Call<ResponseBean<ChallengeBean>> challenge(@Body JsonObject body);

    @POST("signin")
    Call<ResponseBean<SigninBean>> signin(@Body JsonObject body);

    @POST("device-list")
    Call<ResponseBean<DeviceListBean>> getDeviceList(@Body JsonObject body);

    @POST("api/v1/cam-config")
    Call<ResponseBean<DeviceUrlBean>> getDeviceUrl(@Body JsonObject body);

    @POST("v1/fetch-infos")
    Call<ResponseBean<DevicesInfosBean>> getDeviceInfo(@Body JsonObject body);

    @POST("v1/live-sec-token")
    Call<ResponseBean<LiveSrcBean>> getLiveSrcToken(@Body JsonObject body);
}
