package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    设备基础信息
 */
public class DeviceBaseInfoBean{
    public int whiteBalance;
    public int freqValue;
    public String nightMode;
    public String orientationValue;
    public String snapshotUrl;
    public long snapshotTs;
    public String aliasName;
    public String deviceId;
    public String deviceMode;
    public String endpoint;
    public String fwVersion;
    public String newFwVersion;
    public long onlineModified;
    public int onlineStatus;
    public String remoteAddr;
    public long tunnelSyncTime;
    public String vendorCode;
    public String accessKey;
}
