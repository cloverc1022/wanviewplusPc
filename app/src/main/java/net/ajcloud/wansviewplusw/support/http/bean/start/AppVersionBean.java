package net.ajcloud.wansviewplusw.support.http.bean.start;

import java.util.List;

/**
 * Created by mamengchao on 2018/08/06.
 * Function:    App版本信息
 */
public class AppVersionBean {
    public String appName;
    public String appVendorCode;
    public List<Version> apps;

    public static class Version {
        public String os;
        public List<Integer> osVers;
        public String url;
        public String urlType;
        public String versionName;
        public int versionValue;
        public String priority;
        public List<String> mustUpgradeVersions;
        public int mustUpgradeBeforeDays;
        public String releaseDate;
    }
}
