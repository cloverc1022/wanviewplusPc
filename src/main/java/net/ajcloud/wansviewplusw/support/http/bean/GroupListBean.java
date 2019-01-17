package net.ajcloud.wansviewplusw.support.http.bean;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/02.
 * Function:    云存储分组信息
 */
public class GroupListBean {

    public List<GroupInfo> groups;

    public static class GroupInfo {
        /**
         * i帧图片url
         */
        public String intraPicture;
        /**
         * m3u8 url
         */
        public String m3u8Url;
        /**
         * | fps | 帧率 | Int32 | 如15, 25等|
         */
        public String fps;
        /**
         * | encrypt_mode | 加密模式 | Int32 | 0 - 不加密(缺省), 1- AES-128 ECB PKCS5Padding|
         */
        public String encryptMode;
        /**
         * session_key | 会话密钥 | String | - |
         */
        public String sessionKey;
        /**
         * 开始时间戳: 毫秒
         */
        public long tsStart;
        /**
         * 结束时间戳: 毫秒
         */
        public long tsEnd;
        /**
         * 用户展示超期时间
         */
        public String expireViewAt;

        public String keepDays;
        public String _id;
        public String did;
        public String aclid;
        public String groupId;
        public String __v;
    }
}
