package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    设备流信息
 */
public class StreamInfoBean implements Serializable {

    public List<StreamInfo> streams;

    public static class StreamInfo implements Serializable{
        public String bitRate;
        public String frameRate;
        public String localUrl;
        public String no;
        public String quality;
        public int resHeight;
        public int resWidth;
        public String wanUrl;
    }
}
