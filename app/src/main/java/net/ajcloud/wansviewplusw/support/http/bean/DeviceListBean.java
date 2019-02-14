package net.ajcloud.wansviewplusw.support.http.bean;

import net.ajcloud.wansviewplusw.support.http.bean.device.InvitesBean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/08.
 * Function:   设备列表bean
 */
@Deprecated
public class DeviceListBean {
    public List<Device> cameras;
    public List<InvitesBean> userinvites;

    public static class Device {
        public String deviceId;
        public String aliasName;
        public String deviceMode;
        public String vendorCode;
        public String accessPriKey;
        public String accessPubKey;
        public boolean isShare;
        public boolean shareStatus;
        public String bindTs;
    }

}
