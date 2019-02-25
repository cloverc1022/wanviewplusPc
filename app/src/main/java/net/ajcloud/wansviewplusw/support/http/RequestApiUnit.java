package net.ajcloud.wansviewplusw.support.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.Interceptor.CommonInterceptor;
import net.ajcloud.wansviewplusw.support.http.Interceptor.HttpLoggingInterceptor;
import net.ajcloud.wansviewplusw.support.http.Interceptor.OkSignatureInterceptor;
import net.ajcloud.wansviewplusw.support.http.bean.*;
import net.ajcloud.wansviewplusw.support.http.bean.device.DeviceListBean;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;
import net.ajcloud.wansviewplusw.support.http.converters.GsonConverterFactory;
import net.ajcloud.wansviewplusw.support.utils.CipherUtil;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;

public class RequestApiUnit {

    private String deviceName = "windows_pc";
    private String deviceId = "1234567890";
    OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

    public RequestApiUnit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("Retrofit");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        okHttpClient.addInterceptor(new CommonInterceptor());
        okHttpClient.addInterceptor(new OkSignatureInterceptor());
        okHttpClient.addInterceptor(loggingInterceptor);
    }

    public void appStartup(HttpCommonListener<AppStartUpBean> listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sdc.ajcloud.net")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<ResponseBean<AppStartUpBean>> startUpCall = iRequest.appStartUp(ApiConstant.getReqBody(new JsonObject(), null));
        startUpCall.enqueue(new Callback<ResponseBean<AppStartUpBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<AppStartUpBean>> call, Response<ResponseBean<AppStartUpBean>> response) {
                ResponseBean<AppStartUpBean> responseBean = response.body();
                if (responseBean.isSuccess()) {
                    AppStartUpBean bean = responseBean.result;
                    ApiConstant.setUrl(bean.appConfig);
                    if (responseBean.result != null && bean.appVersion != null && !StringUtil.isNullOrEmpty(bean.appVersion.appVendorCode)) {
                        ApiConstant.appVendorCode = bean.appVersion.appVendorCode;
                    }
                    listener.onSuccess(bean);
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<AppStartUpBean>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    public void challenge(String username, String action, final HttpCommonListener<ChallengeBean> listener) {
        final JsonObject dataJson = new JsonObject();
        dataJson.addProperty("username", username);
        dataJson.addProperty("action", action);
        dataJson.addProperty("agentName", deviceName);
        dataJson.addProperty("agentToken", deviceId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_UAC_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest startup = retrofit.create(IRequest.class);
        Call<ResponseBean<ChallengeBean>> challenge = startup.challenge(ApiConstant.getReqBody(dataJson, null));
        challenge.enqueue(new Callback<ResponseBean<ChallengeBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<ChallengeBean>> call, Response<ResponseBean<ChallengeBean>> response) {
                ResponseBean responseBean = response.body();
                if (responseBean.isSuccess()) {
                    listener.onSuccess((ChallengeBean) responseBean.result);
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<ChallengeBean>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    public void signin(final String mail, final String password, final HttpCommonListener<SigninBean> listener) {
        challenge(mail, "signin", new HttpCommonListener<ChallengeBean>() {
            @Override
            public void onSuccess(ChallengeBean bean) {
                JsonObject dataJson = new JsonObject();
                try {
                    byte[] nonce = CipherUtil.getNonce();
                    String encodePassword = CipherUtil.naclEncode(password, bean.clientSecretKey, bean.serverPubKey, nonce);
                    dataJson.addProperty("username", mail);
                    dataJson.addProperty("password", encodePassword);
                    dataJson.addProperty("nonce", new String(Base64.getEncoder().encode(nonce), "UTF-8"));
                    dataJson.addProperty("agentName", deviceName);
                    dataJson.addProperty("agentToken", deviceId);
                    dataJson.addProperty("osName", "android");
                    dataJson.addProperty("grantType", "password");
                    dataJson.addProperty("scope", "all");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiConstant.BASE_UAC_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient.build())
                        .build();
                IRequest startup = retrofit.create(IRequest.class);
                Call<ResponseBean<SigninBean>> signin = startup.signin(ApiConstant.getReqBody(dataJson, null));
                signin.enqueue(new Callback<ResponseBean<SigninBean>>() {
                    @Override
                    public void onResponse(Call<ResponseBean<SigninBean>> call, Response<ResponseBean<SigninBean>> response) {
                        ResponseBean<SigninBean> responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            SigninBean bean = responseBean.result;
                            saveAccount(mail, password, bean);
                            listener.onSuccess(bean);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.message);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBean<SigninBean>> call, Throwable throwable) {
                        listener.onFail(-1, throwable.getMessage());
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                listener.onFail(-1, msg);
            }
        });
    }

    public void getDeviceList(final HttpCommonListener<List<Camera>> listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.BASE_UAC_V2_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest startup = retrofit.create(IRequest.class);
        Call<ResponseBean<DeviceListBean>> getDeviceList = startup.getDeviceList(ApiConstant.getReqBody(new JsonObject(), null));
        getDeviceList.enqueue(new Callback<ResponseBean<DeviceListBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<DeviceListBean>> call, Response<ResponseBean<DeviceListBean>> response) {
                ResponseBean<DeviceListBean> responseBean = response.body();
                if (responseBean.isSuccess()) {
                    DeviceListBean deviceListBean = responseBean.result;
                    DeviceCache.getInstance().upDate(deviceListBean.conDevices, deviceListBean.devGenerals);
                    doGetDeviceList(new ArrayList<>(DeviceCache.getInstance().getAllDevices()));
                    listener.onSuccess(new ArrayList<>(DeviceCache.getInstance().getAllDevices()));
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<DeviceListBean>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    public void getDeviceUrlInfo(List<String> devices, final HttpCommonListener<List<DeviceUrlBean.UrlInfo>> listener) {
        if (devices == null || devices.size() == 0) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (String deviceId : devices) {
            JsonObject data = new JsonObject();
            data.addProperty("deviceId", deviceId);
            jsonArray.add(data);
        }
        jsonObject.add("devices", jsonArray);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sdc.ajcloud.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest startup = retrofit.create(IRequest.class);
        Call<ResponseBean<DeviceUrlBean>> getDeviceUrl = startup.getDeviceUrl(ApiConstant.getReqBody(jsonObject, null));
        getDeviceUrl.enqueue(new Callback<ResponseBean<DeviceUrlBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<DeviceUrlBean>> call, Response<ResponseBean<DeviceUrlBean>> response) {
                ResponseBean responseBean = response.body();
                if (responseBean.isSuccess()) {
                    DeviceUrlBean bean = (DeviceUrlBean) responseBean.result;
                    if (bean.devices != null && bean.devices.size() != 0) {
                        for (DeviceUrlBean.UrlInfo info : bean.devices) {
                            Camera camera = DeviceCache.getInstance().get(info.deviceId);
                            if (camera != null) {
                                camera.setGatewayUrl(info.gatewayUrl);
                                camera.setTunnelUrl(info.tunnelUrl);
                                camera.setCloudStorUrl(info.cloudStorUrl);
                                camera.setEmcUrl(info.emcUrl);
                                camera.setDevCloudStorUrl(info.devCloudStorUrl);
                                camera.setDevEmcUrl(info.devEmcUrl);
                                camera.setDevGatewayUrl(info.devGatewayUrl);
                                camera.setStunServers(info.stunServers);
                                DeviceCache.getInstance().setDeviceUrlTable(camera);
                            }
                        }
                    }
                    listener.onSuccess(bean.devices);
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<DeviceUrlBean>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    public void getDeviceInfo(String url, List<String> deviceIds, boolean isShare, final HttpCommonListener<DevicesInfosBean> listener) {
        JsonObject dataJson = new JsonObject();
        JsonArray devicesJson = new JsonArray();
        for (String deviceId : deviceIds
                ) {
            JsonObject deviceJson = new JsonObject();
            deviceJson.addProperty("did", deviceId);
            deviceJson.add("scopes", new JsonArray());
            deviceJson.addProperty("isShare", isShare);
            devicesJson.add(deviceJson);
        }
        dataJson.add("devices", devicesJson);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest startup = retrofit.create(IRequest.class);
        Call<ResponseBean<DevicesInfosBean>> getDeviceInfo = startup.getDeviceInfo(ApiConstant.getReqBody(dataJson, null));
        getDeviceInfo.enqueue(new Callback<ResponseBean<DevicesInfosBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<DevicesInfosBean>> call, Response<ResponseBean<DevicesInfosBean>> response) {
                ResponseBean<DevicesInfosBean> responseBean = response.body();
                if (responseBean.isSuccess()) {
                    if (responseBean.result != null) {
                        DevicesInfosBean bean = responseBean.result;
                        for (DevicesInfosBean.DeviceInfoBean item : bean.infos
                                ) {
                            Platform.runLater(() -> {
                                DeviceCache.getInstance().add(item.info);
                            });
                        }
                    }
                    listener.onSuccess(responseBean.result);
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<DevicesInfosBean>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    public void getLiveSrcToken(String deviceId, int reqType, int quality, final HttpCommonListener<LiveSrcBean> listener) {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera == null) {
            listener.onFail(-1, "param empty");
            return;
        }

        JsonObject dataJson = new JsonObject();
        dataJson.addProperty("reqType", reqType);
        dataJson.addProperty("quality", quality);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(camera.getGatewayUrl() + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest startup = retrofit.create(IRequest.class);
        Call<ResponseBean<LiveSrcBean>> getDeviceInfo = startup.getLiveSrcToken(ApiConstant.getReqBody(dataJson, deviceId));
        getDeviceInfo.enqueue(new Callback<ResponseBean<LiveSrcBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<LiveSrcBean>> call, Response<ResponseBean<LiveSrcBean>> response) {
                ResponseBean responseBean = response.body();
                if (responseBean.isSuccess()) {
                    LiveSrcBean bean = (LiveSrcBean) responseBean.result;
                    listener.onSuccess(bean);
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<LiveSrcBean>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    public void doLanProbe(String url, String deviceId, final HttpCommonListener<LanProbeBean> listener) {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera == null) {
            listener.onFail(-1, "param empty");
            return;
        }

        JsonObject dataJson = new JsonObject();
        dataJson.addProperty("accessKey", camera.accessKey);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest startup = retrofit.create(IRequest.class);
        Call<ResponseBean<LanProbeBean>> doLanProbe = startup.doLanProbe("", ApiConstant.getReqBody(dataJson, deviceId));
        doLanProbe.enqueue(new Callback<ResponseBean<LanProbeBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<LanProbeBean>> call, Response<ResponseBean<LanProbeBean>> response) {
                ResponseBean responseBean = response.body();
                if (responseBean.isSuccess()) {
                    LanProbeBean bean = (LanProbeBean) responseBean.result;
                    if (StringUtil.equals(bean.deviceId, deviceId)) {
                        listener.onSuccess(bean);
                    } else {
                        listener.onFail(-1, responseBean.message);
                    }
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<LanProbeBean>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    private void doGetDeviceList(List<Camera> devices) {
        List<String> deviceIds = new ArrayList<>();
        for (Camera device : devices) {
            deviceIds.add(device.deviceId);
        }
        getDeviceUrlInfo(deviceIds, new HttpCommonListener<List<DeviceUrlBean.UrlInfo>>() {
            @Override
            public void onSuccess(List<DeviceUrlBean.UrlInfo> bean) {
                if (bean != null && bean.size() > 0) {
                    for (String url : DeviceCache.getInstance().getDeviceUrlTable().keySet()
                            ) {
                        getDeviceInfo(url, DeviceCache.getInstance().getDeviceUrlTable().get(url), false, new HttpCommonListener<DevicesInfosBean>() {
                            @Override
                            public void onSuccess(DevicesInfosBean bean) {
                                if (bean.infos != null && bean.infos.size() > 0) {
                                    for (int i = 0; i < bean.infos.size(); i++) {
                                        if (!StringUtil.isNullOrEmpty(bean.infos.get(i).did)) {
                                            //刷新单条camera信息
//                                            EventBus.getDefault().post(new DeviceRefreshEvent(bean.infos.get(i).did));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFail(int code, String msg) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    /**
     * 云台控制
     *
     * @param action: 0 - 停止, 1 - 向左, 2- 向右, 3 - 向上, 4 - 向下,
     *                5 - 复位(初始位置), 6 - 左右巡航,
     *                7 - 上下巡航, 8 - 向左滑动, 9 - 向右滑动, 10 - 向上滑动, 11-向下滑动
     */
    public void setPtz(String deviceId, int action, final HttpCommonListener<Object> listener) {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera == null) {
            listener.onFail(-1, "param empty");
            return;
        }

        JsonObject dataJson = new JsonObject();
        dataJson.addProperty("action", action);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(camera.getGatewayUrl() + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build();
        IRequest startup = retrofit.create(IRequest.class);
        Call<ResponseBean<Object>> getDeviceInfo = startup.setPtz(ApiConstant.getReqBody(dataJson, deviceId));
        getDeviceInfo.enqueue(new Callback<ResponseBean<Object>>() {
            @Override
            public void onResponse(Call<ResponseBean<Object>> call, Response<ResponseBean<Object>> response) {
                ResponseBean responseBean = response.body();
                if (responseBean.isSuccess()) {
                    listener.onSuccess(responseBean.result);
                } else {
                    listener.onFail(responseBean.getResultCode(), responseBean.message);
                }
            }

            @Override
            public void onFailure(Call<ResponseBean<Object>> call, Throwable throwable) {
                listener.onFail(-1, throwable.getMessage());
            }
        });
    }

    /**
     * 登陆成功后的操作
     */
    private void saveAccount(String mail, String password, SigninBean bean) {
        if (!((StringUtil.isNullOrEmpty(mail) || StringUtil.isNullOrEmpty(password)))) {
            //存储账号信息 TODO
            DeviceCache.getInstance().signinBean = bean;
        }
    }
}
