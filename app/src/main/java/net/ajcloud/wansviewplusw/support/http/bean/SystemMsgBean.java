package net.ajcloud.wansviewplusw.support.http.bean;

import java.util.List;

public class SystemMsgBean {

    public String langCode;
    public String countryCode;
    public String appVendorCode;
    public int category;
    public int type;
    public String title;
    public String content;
    public List<Image> images;
    public List<Avs> avs;
    public List<Acts> acts;
    public long cts;
    public String params;

    public static class  Image{
        public String  url;
        public String  tags;
    }
    public static class  Avs{
        public String  url;
        public String  tags;
    }
    public static class  Acts{
        public String  url;
        public String  tags;
    }
}
