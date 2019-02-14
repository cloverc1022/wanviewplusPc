package net.ajcloud.wansviewplusw.support.http.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/10.
 * Function:    报警消息bean
 */
public class AlarmBean {
    public String did;
    public String dname;
    public String dtzValue;
    public String title;
    public String body;
    public String level;
    public String pts;
    public String ats;
    public String _id;
    public String uid;
    public String aclid;
    public String category;
    public String type;
    public String cdate;
    public String ctime;
    public String cts;
    public String expireViewAt;
    public String expireDelAt;
    public String _v;
    public List<ItemInfoBean> avs;
    public List<ItemInfoBean> images;

    public static class ItemInfoBean {
        public String tags;
        public String url;
    }
}
