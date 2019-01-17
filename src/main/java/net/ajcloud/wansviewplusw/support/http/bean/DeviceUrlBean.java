package net.ajcloud.wansviewplusw.support.http.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/07.
 * Function:    设备接入地址信息
 */
public class DeviceUrlBean {

    public List<UrlInfo> devices;

    public static class UrlInfo {
        public String deviceId;
        public String gatewayUrl;
        public String tunnelUrl;
        public String cloudStorUrl;
        public String emcUrl;
        public String devCloudStorUrl;
        public String devEmcUrl;
        public String devGatewayUrl;
        public String stunServers;
    }
}
