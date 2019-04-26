package net.ajcloud.wansviewplusw.support.utils.play;

import javafx.application.Platform;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.http.ApiConstant;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.LanProbeBean;
import net.ajcloud.wansviewplusw.support.http.bean.LiveSrcBean;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * Created by smilence on 2014/9/10.
 */
public class PoliceHelper /*implements ResponseListener*/ {

    private static final String TAG = PoliceHelper.class.getSimpleName();
    private int mVideoHeight;
    private int mVideoWidth;
    private int mCurrentSize;
    private int playedRequestType;
    private RequestApiUnit deviceApiUnit;

    public interface PoliceControlListener {
        void onCannotPlay();

        void onPlay(String deviceId, int playMethod, String url, int mVideoHeight, int mVideoWidth);

        void onP2pPlay(String deviceId);
    }

    private Camera camera;
    private boolean isRequestToken = false;
    private boolean isP2P = false;
    private PoliceControlListener listener;

    //如果有个策略能够播放，则优先使用这个策略
    public Queue<Integer> playPolices;

    public boolean isRequestToken() {
        return isRequestToken;
    }

    public void setRequestToken(boolean requestToken) {
        isRequestToken = requestToken;
    }

    private void initPolicies() {
        playPolices = new LinkedList<Integer>();
        playPolices.offer(PlayMethod.LAN);
        if (camera.livePolicy.upnp == 1) {
            if (!playPolices.contains(PlayMethod.UPNP))
                playPolices.offer(PlayMethod.UPNP);
        }
        if (camera.livePolicy.p2p == 1) {
            if (!playPolices.contains(PlayMethod.P2P))
                playPolices.offer(PlayMethod.P2P);
        }
        if (camera.livePolicy.relay == 1) {
            if (!playPolices.contains(PlayMethod.RELAY))
                playPolices.offer(PlayMethod.RELAY);
        }
    }

    public PoliceHelper(PoliceControlListener listener) {
        this.listener = listener;
        deviceApiUnit = new RequestApiUnit();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        reset();
    }

    public void tryNextPolicy() {
        try {
            playPolices.remove();
            if (playPolices.size() > 0) {
                getUrlAndPlay();
            } else {
                initPolicies();
                listener.onCannotPlay();
            }
        } catch (NoSuchElementException e) {
            initPolicies();
            listener.onCannotPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String url;

    public void getUrlAndPlay() {
        if (isRequestToken) {
            return;
        }
        isRequestToken = true;
        if (playPolices == null)
            initPolicies();
        try {
            int police;
            police = playPolices.peek();
            if (police == PlayMethod.LAN) {
                playedRequestType = PlayMethod.LAN;
                if (StringUtil.equals(camera.remoteAddr, ApiConstant.wanIp)) {
                    pingLocal();
                } else {
                    isRequestToken = false;
                    tryNextPolicy();
                }
            } else if (police == PlayMethod.UPNP) {
                playedRequestType = PlayMethod.UPNP;
                getLiveSec(2);
            } else if (police == PlayMethod.P2P || police == PlayMethod.RELAY) {
                playedRequestType = PlayMethod.P2P;
                listener.onP2pPlay(camera.deviceId);
            } else {
                isRequestToken = false;
                tryNextPolicy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            initPolicies();
            listener.onCannotPlay();
        }
    }


    public boolean isBusy() {
        return isRequestToken || isP2P;
    }

    public void reset() {
        isRequestToken = false;
        initPolicies();
    }

    //LAN
    private void pingLocal() {
        //socket
//        new LanDetectionUnit().connect(virtualCamera.localIp, "80", new LanDetectionUnit.LanDetectionCallback() {
//            @Override
//            public void success() {
//                getLiveSec(1);
//            }
//
//            @Override
//            public void fail() {
//                isRequestToken = false;
//                tryNextPolicy();
//            }
//        });
        //http
        deviceApiUnit.doLanProbe(camera.networkConfig.localDirectProbeUrl, camera.deviceId, new HttpCommonListener<LanProbeBean>() {
            @Override
            public void onSuccess(LanProbeBean bean) {
                getLiveSec(1);
            }

            @Override
            public void onFail(int code, String msg) {
                Platform.runLater(() -> {
                    isRequestToken = false;
                    tryNextPolicy();
                });
            }
        });
    }

    //获取播放地址，token
    private void getLiveSec(int reqType) {
        new Thread(() -> deviceApiUnit.getLiveSrcToken(camera.deviceId, reqType, camera.getCurrentQuality(), new HttpCommonListener<LiveSrcBean>() {
            @Override
            public void onSuccess(LiveSrcBean bean) {
                Platform.runLater(() -> {
                    if (bean.stream != null) {
                        if (reqType == 1) {
                            listener.onPlay(camera.deviceId, playedRequestType, bean.stream.localUrl, bean.stream.resHeight, bean.stream.resWidth);
                        } else if (reqType == 2) {
                            listener.onPlay(camera.deviceId, playedRequestType, bean.stream.wanUrl, bean.stream.resHeight, bean.stream.resWidth);
                        }
                    } else {
                        isRequestToken = false;
                        tryNextPolicy();
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                Platform.runLater(() -> {
                    isRequestToken = false;
                    tryNextPolicy();
                });
            }
        })).start();
    }
}
