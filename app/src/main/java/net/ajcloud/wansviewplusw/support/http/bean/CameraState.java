package net.ajcloud.wansviewplusw.support.http.bean;

import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceInfoDictionary;

/**
 * Created by mamengchao on 2018/10/10.
 * Function:    camera状态，四分屏界面用
 */
public class CameraState {
    public int id;
    public String deviceId;
    public String name;
    public boolean enable;
    public int state;     //0：暂停 1：播放
    public int onlineStatus;     // 离线 - 1, 在线 - 2, 升级中 - 4
    public int refreshStatus;  //刷新状态   0：正在刷新    1：成功    2：失败

    public CameraState(int id) {
        enable = false;
        this.id = id;
    }

    public void add(Camera camera) {
        enable = true;
        this.state = 1;
        this.deviceId = camera.deviceId;
        this.name = DeviceInfoDictionary.getNameByDevice(camera);
        this.onlineStatus = camera.getOnlineStatus();
        this.refreshStatus = camera.getRefreshStatus();
    }

    public void reset() {
        enable = false;
        state = 0;
        this.deviceId = null;
        this.name = null;
        this.onlineStatus = 0;
        this.refreshStatus = 0;
    }
}
