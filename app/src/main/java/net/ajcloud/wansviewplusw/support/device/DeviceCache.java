package net.ajcloud.wansviewplusw.support.device;

import net.ajcloud.wansviewplusw.support.http.bean.DeviceConfigBean;
import net.ajcloud.wansviewplusw.support.http.bean.SigninBean;
import net.ajcloud.wansviewplusw.support.http.bean.device.ConDeviceBean;
import net.ajcloud.wansviewplusw.support.http.bean.device.DeviceGeneralsBean;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.Trans2PinYin;

import java.util.*;

/**
 * Created by mamengchao on 2018/06/04.
 * Function: 设备缓存
 */
public class DeviceCache {


    public SigninBean signinBean;

    private Map<String, Camera> hashTable = Collections.synchronizedMap(new LinkedHashMap<>());

    //<url,deviceIds>
    private Hashtable<String, List<String>> deviceUrlTable = new Hashtable<>();

    private DeviceCache() {
    }

    public static DeviceCache getInstance() {
        return DeviceCacheHolder.instance;
    }

    private static class DeviceCacheHolder {
        private static final DeviceCache instance = new DeviceCache();
    }

    /**
     * 只在绑定设备时单独添加
     */
    public void add(Camera camera) {
        Camera cameraOrigin = hashTable.get(camera.deviceId);
        if (cameraOrigin == null) {
            hashTable.put(camera.deviceId, camera);
            camera.setRefreshStatus(0);
//            if (camera.aliasName != null) {
//                camera.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(camera).trim()).toLowerCase();
//            }
        }
    }

    public void upDate(List<ConDeviceBean> conDevices, List<DeviceGeneralsBean> devGenerals) {
        if (conDevices == null || conDevices.size() == 0 ||
                devGenerals == null || devGenerals.size() == 0) {
            clear();
        } else {
            //去掉设备
            List<Camera> tmpList = new ArrayList<>(hashTable.values());
            outer:
            for (Camera originalCamera : tmpList) {
                for (ConDeviceBean bean : conDevices) {
                    if (StringUtil.equals(bean.deviceId, originalCamera.deviceId)) {
                        continue outer;
                    }
                }
                remove(originalCamera);
            }
            //新增设备
            tmpList.clear();
            tmpList.addAll(hashTable.values());
            outer:
            for (ConDeviceBean bean : conDevices) {
                for (Camera originalCamera : tmpList) {
                    if (StringUtil.equals(bean.deviceId, originalCamera.deviceId)) {
                        //更新device-list接口数据
                        Camera camera = this.get(originalCamera.deviceId);
                        camera.aliasName = bean.aliasName;
                        camera.conStatus = bean.conStatus;
                        camera.conType = bean.conType;
                        camera.bindTs = bean.conTs;
                        continue outer;
                    }
                }
                for (DeviceGeneralsBean deviceGeneralsBean :
                        devGenerals) {
                    if (StringUtil.equals(bean.deviceId, deviceGeneralsBean.deviceId)) {
                        add(new Camera(bean, deviceGeneralsBean));
                        continue outer;
                    }
                }
            }
        }
    }

    /**
     * 请求fetch-info时调用
     */
    public void add(DeviceConfigBean bean) {
        Camera camera = new Camera(bean);
        if (camera.deviceId != null) {
            Camera cameraOrigin = hashTable.get(camera.deviceId);
            if (cameraOrigin == null) {
//                hashTable.put(camera.deviceId, camera);
            } else {
                mergeDeviceInfo(cameraOrigin, camera);
            }
        }
    }

    //合并设备信息
    private void mergeDeviceInfo(Camera cameraOrigin, Camera camera) {
        if (!StringUtil.equals(cameraOrigin.aliasName, camera.aliasName)) {
            if (camera.aliasName != null) {
                cameraOrigin.sortStr = Trans2PinYin.trans2PinYin(DeviceInfoDictionary.getNameByDevice(camera).trim()).toLowerCase();
            } else {
                cameraOrigin.sortStr = "";
            }
        }
        //获取信息成功
        cameraOrigin.setRefreshStatus(1);
        cameraOrigin.whiteBalance = camera.whiteBalance;
        cameraOrigin.freqValue = camera.freqValue;
        cameraOrigin.nightMode = camera.nightMode;
        cameraOrigin.orientationValue = camera.orientationValue;
        cameraOrigin.onlineModified = camera.onlineModified;
        cameraOrigin.onlineStatus = camera.onlineStatus;
        cameraOrigin.tunnelSyncTime = camera.tunnelSyncTime;
        cameraOrigin.deviceId = camera.deviceId;
        cameraOrigin.deviceMode = camera.deviceMode;
        cameraOrigin.endpoint = camera.endpoint;
        cameraOrigin.fwVersion = camera.fwVersion;
        cameraOrigin.remoteAddr = camera.remoteAddr;
        cameraOrigin.vendorCode = camera.vendorCode;
        cameraOrigin.snapshotUrl = camera.snapshotUrl;
        cameraOrigin.snapshotTs = camera.snapshotTs;
        cameraOrigin.accessKey = camera.accessKey;
        cameraOrigin.newFwVersion = camera.newFwVersion;

        cameraOrigin.capability = camera.capability;
        cameraOrigin.streamConfig = camera.streamConfig;
        cameraOrigin.livePolicy = camera.livePolicy;
        cameraOrigin.networkConfig = camera.networkConfig;
        cameraOrigin.setViewAnglesConfig(camera.getViewAnglesConfig());
        cameraOrigin.localStorConfig = camera.localStorConfig;
        cameraOrigin.moveMonitorConfig = camera.moveMonitorConfig;
        cameraOrigin.soundMonitorConfig = camera.soundMonitorConfig;
        cameraOrigin.cloudStorConfig = camera.cloudStorConfig;
        cameraOrigin.audioConfig = camera.audioConfig;
        cameraOrigin.pictureConfig = camera.pictureConfig;
        cameraOrigin.timeConfig = camera.timeConfig;
        cameraOrigin.cloudStorPlan = camera.cloudStorPlan;
        cameraOrigin.newFwversion = camera.newFwversion;
        cameraOrigin.tfcardConfig = camera.tfcardConfig;
        cameraOrigin.onvifConfig = camera.onvifConfig;
        cameraOrigin.localAccountConfig = camera.localAccountConfig;
    }

    public void remove(Camera camera) {
        remove(camera.deviceId);
    }

    public void remove(String devID) {
        hashTable.remove(devID);

        Hashtable<String, List<String>> tmp = (Hashtable<String, List<String>>) deviceUrlTable.clone();
        outer:
        for (List<String> deviceIds : tmp.values()
                ) {
            for (String deviceId : deviceIds
                    ) {
                if (StringUtil.equals(devID, deviceId)) {
                    deviceIds.remove(deviceId);
                    break outer;
                }
            }
        }
        deviceUrlTable.clear();
        deviceUrlTable.putAll(tmp);
    }

    public Camera get(String deviceId) {
        return StringUtil.isNullOrEmpty(deviceId) ? null : hashTable.get(deviceId);
    }

    /**
     * except share device
     */
    public List<Camera> getDevices() {
        List<Camera> devices = new ArrayList<>();
        for (Camera camera :
                hashTable.values()) {
            if (camera.conType != 1) {
                devices.add(camera);
            }
        }
        return devices;
    }

    /**
     * contain share device
     */
    public Collection<Camera> getAllDevices() {
        return hashTable.values();
    }

    /**
     * except share device
     */
    public List<String> getDeviceIds() {
        List<String> deviceIds = new ArrayList<>();
        for (Camera camera :
                hashTable.values()) {
            if (camera.conType != 1) {
                deviceIds.add(camera.deviceId);
            }
        }
        return deviceIds;
    }

    /**
     * contain share device
     */
    public List<String> getAllDeviceIds() {
        return new ArrayList<>(hashTable.keySet());
    }

    public int getCounts() {
        return hashTable.size();
    }

    public boolean contains(String deviceID) {
        return get(deviceID) != null;
    }

    public void clear() {
        hashTable.clear();
    }

    public void clearUrl() {
        deviceUrlTable.clear();
    }

    public Hashtable<String, List<String>> getDeviceUrlTable() {
        return deviceUrlTable;
    }

    public void setDeviceUrlTable(Camera camera) {
        if (deviceUrlTable.containsKey(camera.getGatewayUrl())) {
            for (String deviceId : deviceUrlTable.get(camera.getGatewayUrl())
                    ) {
                if (StringUtil.equals(deviceId, camera.deviceId)) {
                    return;
                }
            }
            deviceUrlTable.get(camera.getGatewayUrl()).add(camera.deviceId);
        } else {
            List<String> deviceIds = new ArrayList<>();
            deviceIds.add(camera.deviceId);
            deviceUrlTable.put(camera.getGatewayUrl(), deviceIds);
        }
    }
}
