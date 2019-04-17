package net.ajcloud.wansviewplusw.support.utils.play;


import com.google.gson.JsonObject;
import com.sun.istack.internal.NotNull;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.ApiConstant;
import net.ajcloud.wansviewplusw.support.http.IRequest;
import net.ajcloud.wansviewplusw.support.http.Interceptor.CommonInterceptor;
import net.ajcloud.wansviewplusw.support.http.Interceptor.HttpLoggingInterceptor;
import net.ajcloud.wansviewplusw.support.http.Interceptor.OkSignatureInterceptor;
import net.ajcloud.wansviewplusw.support.http.bean.LinkInfo;
import net.ajcloud.wansviewplusw.support.http.bean.LiveSrcBean;
import net.ajcloud.wansviewplusw.support.http.bean.ResponseBean;
import net.ajcloud.wansviewplusw.support.http.converters.GsonConverterFactory;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import okhttp3.OkHttpClient;
import org.tcprelay.Tcprelay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by mamengchao on 2019/03/08.
 * Function:    提前建链
 */
public class TcprelayHelper {

    protected final String TAG = this.getClass().getSimpleName();

    private int defaultPorts = 8888;

    private Tcprelay tcprelay;

    private ArrayList<Integer> ports;

    private Map<String, LinkInfo> runningMap = Collections.synchronizedMap(new HashMap<>());
    private Map<String, LinkInfo> linksMap = Collections.synchronizedMap(new HashMap<>());

    public static TcprelayHelper getInstance() {
        return TcprelayHelper.TcprelayHelperHolder.instance;
    }

    private static class TcprelayHelperHolder {
        private static final TcprelayHelper instance = new TcprelayHelper();
    }

    private TcprelayHelper() {
        tcprelay = new Tcprelay();
    }

    public void init(Camera camera) {
        if (!linksMap.containsKey(camera.deviceId) &&
                (!runningMap.containsKey(camera.deviceId) ||
                        (runningMap.containsKey(camera.deviceId) && !runningMap.get(camera.deviceId).isValid()))) {
            initLink(camera.deviceId, camera.getCurrentQuality(), null);
        }
    }

    public void reset() {
        for (LinkInfo linkInfo : linksMap.values()
        ) {
            tcprelay.relaydisconnect(linkInfo.getNum());
        }
        runningMap.clear();
        linksMap.clear();
        for (Camera camera :
                DeviceCache.getInstance().getAllDevices()) {
            initLink(camera.deviceId, camera.getCurrentQuality(), null);
        }
    }

    public void deinit() {
        for (LinkInfo linkInfo : linksMap.values()
        ) {
            tcprelay.relaydisconnect(linkInfo.getNum());
        }
        linksMap.clear();
        runningMap.clear();
    }

    public void disconnect(String deviceId) {
        if (runningMap.containsKey(deviceId)) {
            runningMap.get(deviceId).setValid(false);
            runningMap.remove(deviceId);
        }
        if (linksMap.containsKey(deviceId)) {
            tcprelay.relaydisconnect(linksMap.get(deviceId).getNum());
            linksMap.remove(deviceId);
        }
    }

    /**
     * step1：初始化建链设备
     */
    public void initLink(@NotNull String deviceId, int quality, ConnectCallback connectCallback) {
        new Thread(() -> {
            WLog.w(TAG, "initLink--------start");
            Camera camera = DeviceCache.getInstance().get(deviceId);
            if (camera == null) {
                WLog.w(TAG, "initLink--------camera==null");
                return;
            }
            linksMap.remove(deviceId);
            runningMap.remove(deviceId);
            LinkInfo newLinkInfo = new LinkInfo(deviceId, quality, connectCallback);
            runningMap.put(deviceId, newLinkInfo);
            getLiveSec(newLinkInfo);
        }).start();
    }

    /**
     * step2：请求播放信息
     */
    private void getLiveSec(LinkInfo linkInfo) {
        WLog.w(TAG, "initLink--------getLiveSec");

        Camera camera = DeviceCache.getInstance().get(linkInfo.getDeviceId());
        if (linkInfo.isValid()) {
            try {
                JsonObject dataJson = new JsonObject();
                dataJson.addProperty("reqType", 3);
                dataJson.addProperty("quality", linkInfo.getQuality());

                OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("Retrofit");
                loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
                loggingInterceptor.setColorLevel(Level.INFO);
                okHttpClient.addInterceptor(new CommonInterceptor());
                okHttpClient.addInterceptor(new OkSignatureInterceptor());
                okHttpClient.addInterceptor(loggingInterceptor);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(camera.getGatewayUrl() + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient.build())
                        .build();
                IRequest startup = retrofit.create(IRequest.class);
                Call<ResponseBean<LiveSrcBean>> getDeviceInfo = startup.getLiveSrcToken(ApiConstant.getReqBody(dataJson, linkInfo.getDeviceId()));
                getDeviceInfo.enqueue(new Callback<ResponseBean<LiveSrcBean>>() {
                    @Override
                    public void onResponse(Call<ResponseBean<LiveSrcBean>> call, retrofit2.Response<ResponseBean<LiveSrcBean>> response) {
                        ResponseBean responseBean = response.body();
                        if (responseBean.isSuccess()) {
                            LiveSrcBean bean = (LiveSrcBean) responseBean.result;
                            if (linkInfo.isValid()) {
                                linkInfo.setServerIp(bean.reqServer);
                                if (bean.stream != null) {
                                    linkInfo.setLocalUrl(bean.stream.localUrl);
                                }
                                connect(linkInfo);
                            }
                        } else {
                            WLog.w(TAG, "initLink--------getLiveSec_fail");
                            linkInfo.setStatus(2);
                            linksMap.put(linkInfo.getDeviceId(), linkInfo);
                            runningMap.remove(linkInfo.getDeviceId());
                            if (linkInfo.getConnectCallback() != null) {
                                linkInfo.getConnectCallback().onFail();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBean<LiveSrcBean>> call, Throwable throwable) {
                        WLog.w(TAG, "initLink--------getLiveSec_fail");
                        linkInfo.setStatus(2);
                        linksMap.put(linkInfo.getDeviceId(), linkInfo);
                        runningMap.remove(linkInfo.getDeviceId());
                        if (linkInfo.getConnectCallback() != null) {
                            linkInfo.getConnectCallback().onFail();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                WLog.w(TAG, "initLink--------getLiveSec_fail");
                linkInfo.setStatus(2);
                linksMap.put(linkInfo.getDeviceId(), linkInfo);
                runningMap.remove(linkInfo.getDeviceId());
                if (linkInfo.getConnectCallback() != null) {
                    linkInfo.getConnectCallback().onFail();
                }
            }
        }
    }

    /**
     * step3：建立连接
     */
    private void connect(LinkInfo linkInfo) {
        WLog.w(TAG, "initLink--------connect");
        Camera camera = DeviceCache.getInstance().get(linkInfo.getDeviceId());
        if (linkInfo.isValid()) {
            //获取port
            int port = getPort();
            linkInfo.setPort(port);
            WLog.w(TAG, "initLink--------port:" + port);
            //链接
            int num = tcprelay.relayconnect(linkInfo.getDeviceId(), camera.getStunServers(), linkInfo.getServerIp(), port);
            if (linkInfo.isValid()) {
                if (num > 0) {
                    //成功
                    StringBuilder url = new StringBuilder();
                    String token = linkInfo.getLocalUrl().split("live")[1];
                    url.append("rtsp://");
                    url.append("127.0.0.1:");
                    url.append(port);
                    url.append("/live");
                    url.append(token);
                    linkInfo.setNum(num);
                    linkInfo.setUrl(url.toString());
                    WLog.w(TAG, "initLink--------connect_success-----num:" + num);
                    WLog.w(TAG, "initLink--------connect_success-----url:" + url.toString());
                    linkInfo.setStatus(1);
                    linksMap.put(linkInfo.getDeviceId(), linkInfo);
                    runningMap.remove(linkInfo.getDeviceId());
                    if (linkInfo.getConnectCallback() != null) {
                        linkInfo.getConnectCallback().onSuccess(linkInfo.getUrl());
                    }
                } else {
                    //失败
                    WLog.w(TAG, "initLink--------connect_fail-----num:" + num);
                    tcprelay.relaydisconnect(num);
                    linkInfo.setStatus(2);
                    linksMap.put(linkInfo.getDeviceId(), linkInfo);
                    runningMap.remove(linkInfo.getDeviceId());
                    if (linkInfo.getConnectCallback() != null) {
                        linkInfo.getConnectCallback().onFail();
                    }
                }
            }
        }
    }

    /**
     * 获取播放链接
     */
    public void getPlayUrl(@NotNull String deviceId, int quality, ConnectCallback connectCallback) {
        LinkInfo linkInfo = linksMap.get(deviceId);
        if (linkInfo == null) {
            LinkInfo runningLinkInfo = runningMap.get(deviceId);
            if (runningLinkInfo == null) {
                initLink(deviceId, quality, connectCallback);
            } else {
                if (!runningLinkInfo.isValid() || runningLinkInfo.getQuality() != quality) {
                    runningLinkInfo.setValid(false);
                    initLink(deviceId, quality, connectCallback);
                } else {
                    runningLinkInfo.setConnectCallback(connectCallback);
                }
            }
        } else {
            linkInfo.setConnectCallback(connectCallback);
            if (linkInfo.getStatus() == 1) {
                if (linkInfo.getQuality() == quality) {
                    linkInfo.getConnectCallback().onSuccess(linkInfo.getUrl());
                } else {
                    initLink(deviceId, quality, connectCallback);
                }
            } else if (linkInfo.getStatus() == 2) {
                initLink(deviceId, quality, connectCallback);
            }
        }
    }

    synchronized public Integer getPort() {
        if (ports == null || ports.size() == 0) {
            return defaultPorts++;
        } else {
            int port = ports.get(0);
            ports.remove(0);
            return port;
        }
    }

    public void addPorts(int port) {
        if (ports == null) {
            ports = new ArrayList<>();
        }
        ports.add(port);
    }

    public Tcprelay getTcprelay() {
        return tcprelay;
    }

    public interface ConnectCallback {
        void onSuccess(String url);

        void onFail();
    }
}
