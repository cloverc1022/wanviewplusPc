package net.ajcloud.wansviewplusw.support.http.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/10/08.
 * Function:
 */
public class DevicesInfosBean {

    public List<DeviceInfoBean> infos;

    public static class DeviceInfoBean {
        public String did;
        public DeviceConfigBean info;
    }
}
