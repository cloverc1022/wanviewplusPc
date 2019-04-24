package net.ajcloud.wansviewplusw.support.http;

import com.google.gson.JsonObject;
import net.ajcloud.wansviewplusw.support.http.bean.*;
import net.ajcloud.wansviewplusw.support.http.bean.device.DeviceListBean;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;
import retrofit2.Call;
import retrofit2.http.*;

import java.io.File;

public interface IRequest {
    @POST("/api/v1/app-startup")
    Call<ResponseBean<AppStartUpBean>> appStartUp(@Body JsonObject body);

    @POST("challenge")
    Call<ResponseBean<ChallengeBean>> challenge(@Body JsonObject body);

    @POST("signin")
    Call<ResponseBean<SigninBean>> signin(@Body JsonObject body);

    @POST("signout")
    Call<ResponseBean<Object>> signout(@Body JsonObject body);

    @POST("device-list")
    Call<ResponseBean<DeviceListBean>> getDeviceList(@Body JsonObject body);

    @POST("api/v1/cam-config")
    Call<ResponseBean<DeviceUrlBean>> getDeviceUrl(@Body JsonObject body);

    @POST("v1/fetch-infos")
    Call<ResponseBean<DevicesInfosBean>> getDeviceInfo(@Body JsonObject body);

    @POST("v1/live-sec-token")
    Call<ResponseBean<LiveSrcBean>> getLiveSrcToken(@Body JsonObject body);

    @POST("{url}")
    Call<ResponseBean<LanProbeBean>> doLanProbe(@Path("url") String url, @Body JsonObject body);

    /**
     * 云台控制
     * <p>
     * action: 0 - 停止, 1 - 向左, 2- 向右, 3 - 向上, 4 - 向下,
     * 5 - 复位(初始位置), 6 - 左右巡航,
     * 7 - 上下巡航, 8 - 向左滑动, 9 - 向右滑动, 10 - 向上滑动, 11-向下滑动
     */
    @POST("v1/ptz-control")
    Call<ResponseBean<Object>> setPtz(@Body JsonObject body);

    /**
     * 快照
     */
    @POST("v1/snapshot")
    Call<ResponseBean<Object>> doSnapshot(@Body JsonObject body);

    @GET
    Call<File> downloadFile(@Url String url);

    @POST("refresh-token")
    Call<RefreshTokenBean> refreshToken(@Body JsonObject body);
}
