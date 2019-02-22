package net.ajcloud.wansviewplusw.support.device;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.ajcloud.wansviewplusw.support.http.bean.*;
import net.ajcloud.wansviewplusw.support.http.bean.device.ConDeviceBean;
import net.ajcloud.wansviewplusw.support.http.bean.device.DeviceGeneralsBean;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by mamengchao on 2018/06/07.
 * Function:    摄像机实体类
 */
public class Camera implements Serializable {
    private static final long serialVersionUID = 1L;
    public int whiteBalance;
    public int freqValue;
    public String nightMode;
    public String orientationValue;
    public String aliasName;
    public String deviceId;
    public String deviceMode;
    public String endpoint;
    public String fwVersion;
    public String newFwVersion;
    public long onlineModified;
    public int onlineStatus;     // 离线 - 1, 在线 - 2, 升级中 - 4
    public String remoteAddr;
    public long tunnelSyncTime;
    public String vendorCode;
    public String snapshotUrl;
    public long snapshotTs;
    public String accessKey;
    public boolean hasSnapShot = false;
    public String bindTs;
    public int deviceType;      //设备类型，1-普通摄像头，2-门锁，3-电池摄像头，4-可视门铃，8-网关
    public int conStatus;         //当前的关联状态，0-无效，1-有效
    public int conType = 0;          //和账户的关联类型，0-绑定，1-分享，2-子设备

    public CapabilityBean capability;
    public StreamInfoBean streamConfig;
    public LivePolicyBean livePolicy;
    public NetworkInfoBean networkConfig;
    private ViewAnglesBean viewAnglesConfig;
    public LocalStorBean localStorConfig;
    public MoveMonitorBean moveMonitorConfig;
    public SoundMonitorBean soundMonitorConfig;
    public CloudStorBean cloudStorConfig;
    public AudioInfoBean audioConfig;
    public PictureInfoBean pictureConfig;
    public DeviceTimeBean timeConfig;
    public CloudStorPlanBean cloudStorPlan;
    public NewVersionBean newFwversion;
    public TfcardConfigBean tfcardConfig;
    public OnvifConfigBean onvifConfig;
    public LocalAccountConfig localAccountConfig;

    private StringProperty refreshStatus = new SimpleStringProperty();  //刷新状态   0：正在刷新    1：成功    2：失败
    public String sortStr;  //用于排序
    private String gatewayUrl;
    private String tunnelUrl;
    private String cloudStorUrl;
    private String emcUrl;
    private String accessPriKey;
    private String accessPubKey;
    private String devCloudStorUrl;
    private String devEmcUrl;
    private String devGatewayUrl;
    private String stunServers;

    public Camera() {

    }

//    public Camera(@NotNull String deviceId, String name) {
//        this.deviceId = deviceId;
//        this.aliasName = name;
//    }

    public Camera(ConDeviceBean conDeviceBean, DeviceGeneralsBean deviceGeneralsBean) {
        this.deviceId = conDeviceBean.deviceId;
        this.aliasName = conDeviceBean.aliasName;
        this.deviceMode = deviceGeneralsBean.deviceMode;
        this.vendorCode = deviceGeneralsBean.vendorCode;
        this.accessPriKey = deviceGeneralsBean.accessPriKey;
        this.accessPubKey = deviceGeneralsBean.accessPubKey;
        this.bindTs = conDeviceBean.conTs;
        this.deviceType = conDeviceBean.deviceType;
        this.conStatus = conDeviceBean.conStatus;
        this.conType = conDeviceBean.conType;
    }

    public Camera(DeviceConfigBean bean) {
        if (bean.base != null) {
            whiteBalance = bean.base.whiteBalance;
            freqValue = bean.base.freqValue;
            nightMode = bean.base.nightMode;
            orientationValue = bean.base.orientationValue;
            onlineModified = bean.base.onlineModified;
            onlineStatus = bean.base.onlineStatus;
            tunnelSyncTime = bean.base.tunnelSyncTime;
            deviceId = bean.base.deviceId;
            deviceMode = bean.base.deviceMode;
            endpoint = bean.base.endpoint;
            fwVersion = bean.base.fwVersion;
            newFwVersion = bean.base.newFwVersion;
            remoteAddr = bean.base.remoteAddr;
            vendorCode = bean.base.vendorCode;
            snapshotUrl = bean.base.snapshotUrl;
            snapshotTs = bean.base.snapshotTs;
            accessKey = bean.base.accessKey;

            capability = bean.capability;
            streamConfig = bean.streamConfig;
            livePolicy = bean.livePolicy;
            networkConfig = bean.networkConfig;
            setViewAnglesConfig(bean.viewAnglesConfig);
            localStorConfig = bean.localStorConfig;
            moveMonitorConfig = bean.moveMonitorConfig;
            soundMonitorConfig = bean.soundMonitorConfig;
            cloudStorConfig = bean.cloudStorConfig;
            audioConfig = bean.audioConfig;
            pictureConfig = bean.pictureConfig;
            timeConfig = bean.timeConfig;
            cloudStorPlan = bean.cloudStorPlan;
            newFwversion = bean.newFwversion;
            tfcardConfig = bean.tfcardConfig;
            onvifConfig = bean.onvifConfig;
            localAccountConfig = bean.localAccountConfig;
        }
    }

    public String getRefreshStatus() {
        return refreshStatus.get();
    }

    public StringProperty refreshStatusProperty() {
        return refreshStatus;
    }

    public void setRefreshStatus(int refreshStatus) {
        //0：正在刷新    1：成功    2：失败
        if (refreshStatus == 0) {
            this.refreshStatus.set("Connecting");
        }
    }

    public String getGatewayUrl() {
        return StringUtil.isNullOrEmpty(gatewayUrl) ? "" : gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getTunnelUrl() {
        return tunnelUrl;
    }

    public void setTunnelUrl(String tunnelUrl) {
        this.tunnelUrl = tunnelUrl;
    }

    public String getCloudStorUrl() {
        return cloudStorUrl;
    }

    public void setCloudStorUrl(String cloudStorUrl) {
        this.cloudStorUrl = cloudStorUrl;
    }

    public String getEmcUrl() {
        return emcUrl;
    }

    public void setEmcUrl(String emcUrl) {
        this.emcUrl = emcUrl;
    }

    public String getAccessPriKey() {
        return accessPriKey;
    }

    public void setAccessPriKey(String accessPriKey) {
        this.accessPriKey = accessPriKey;
    }

    public String getAccessPubKey() {
        return accessPubKey;
    }

    public void setAccessPubKey(String accessPubKey) {
        this.accessPubKey = accessPubKey;
    }

    public String getDevCloudStorUrl() {
        return devCloudStorUrl;
    }

    public void setDevCloudStorUrl(String devCloudStorUrl) {
        this.devCloudStorUrl = devCloudStorUrl;
    }

    public String getDevEmcUrl() {
        return devEmcUrl;
    }

    public void setDevEmcUrl(String devEmcUrl) {
        this.devEmcUrl = devEmcUrl;
    }

    public String getDevGatewayUrl() {
        return devGatewayUrl;
    }

    public void setDevGatewayUrl(String devGatewayUrl) {
        this.devGatewayUrl = devGatewayUrl;
    }

    public String getStunServers() {
        return stunServers;
    }

    public void setStunServers(String stunServers) {
        this.stunServers = stunServers;
    }

    public ViewAnglesBean getViewAnglesConfig() {
        return viewAnglesConfig;
    }

    public void setViewAnglesConfig(ViewAnglesBean viewAnglesConfig) {
        sortViewAngles(viewAnglesConfig);
        this.viewAnglesConfig = viewAnglesConfig;
    }

    public Object deepClone() {
        try {
            // 序列化
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(this);

            // 反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sortViewAngles(ViewAnglesBean bean) {
        Collections.sort(bean.viewAngles, new Comparator<ViewAnglesBean.ViewAngle>() {
            @Override
            public int compare(ViewAnglesBean.ViewAngle o1, ViewAnglesBean.ViewAngle o2) {
                if (o1.viewAngle > o2.viewAngle) {
                    return 1;
                } else if (o1.viewAngle < o2.viewAngle) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

    }

    public boolean isOnline() {
        return onlineStatus == 2;
    }

    /**
     * 云存储是否失效
     */
    public boolean isCloudInService() {
        return !(cloudStorPlan == null ||
                StringUtil.isNullOrEmpty(cloudStorPlan.sku) ||
                System.currentTimeMillis() > Long.parseLong(cloudStorPlan.validTsEnd));
    }

    /**
     * 固件版本
     */
    public boolean hasFwNewVersion() {
        if (StringUtil.isNullOrEmpty(fwVersion) || StringUtil.isNullOrEmpty(newFwVersion) || !isOnline()) {
            return false;
        } else {
            return newFwVersion.compareTo(fwVersion) > 0;
        }
    }

    /**
     * 是否有tf卡
     */
    public boolean hasTfCard() {
        return !(tfcardConfig == null || tfcardConfig.status == 1);
    }

    /**
     * 是否是分享设备
     */
    public boolean isShare() {
        return this.conType == 1;
    }

    /**
     * 是否有效
     */
    public boolean isVaild() {
        return this.conStatus == 1;
    }

    /**
     * 判断是否带云台
     */
    public boolean hasPtz() {
        if (this.capability != null && this.capability.ptz == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否支持TF卡
     */
    public boolean supportTfCard() {
        if (this.capability != null &&
                !StringUtil.isNullOrEmpty(this.capability.localStorageTypes) &&
                this.capability.localStorageTypes.toLowerCase().contains("tf")) {
            return true;
        } else {
            return false;
        }
    }
}
