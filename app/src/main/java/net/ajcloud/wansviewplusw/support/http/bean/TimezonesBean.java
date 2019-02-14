package net.ajcloud.wansviewplusw.support.http.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/04.
 * Function:时区信息
 */
public class TimezonesBean {
    public List<TimezoneInfo> timeZones;

    public static class TimezoneInfo {
        public String tzName;
        public String tzGmt;
        public String tzValue;
        public String en;
        public String zh;
        public String fr;
        public String es;
        public String pt;
        public String ar;
        public String ja;
        public String ko;
        public String de;
    }
}
