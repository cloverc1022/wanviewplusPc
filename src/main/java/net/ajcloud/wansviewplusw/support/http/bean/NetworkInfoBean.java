package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    网络配置信息
 */
public class NetworkInfoBean implements Serializable {
    public String ethMac;
    public String localDirectProbeUrl;
    public String localIp;
    public String localIpMask;
    public String wanIp;
    public String wlanMac;
    public String ssid;
    public String netLinkType;
}
