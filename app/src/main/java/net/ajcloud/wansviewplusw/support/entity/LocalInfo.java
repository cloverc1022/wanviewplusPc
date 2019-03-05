package net.ajcloud.wansviewplusw.support.entity;

import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

public class LocalInfo {

    private LocalInfo() {
    }

    public static LocalInfo getInstance() {
        return LocalInfoHolder.instance;
    }

    private static class LocalInfoHolder {
        private static final LocalInfo instance = new LocalInfo();
    }

    //设备唯一Id
    public String deviceId;
    //设备名
    public String deviceName;
    private String appLang;
    public int timeZone;
    public String timeZoneName;

    public void setAppLang(String appLang) {
        this.appLang = appLang;
    }

    public String getAppLang() {
        if (StringUtil.isNullOrEmpty(appLang)) {
            return "en";
        }
        if (StringUtil.equals(appLang, "de")) {
            return "de";
        } else if (StringUtil.equals(appLang, "fr")) {
            return "fr";
        } else {
            return "en";
        }
    }

    public String getSystemLang() {
        if (StringUtil.isNullOrEmpty(appLang)) {
            return "en";
        } else {
            return appLang;
        }
    }
}
