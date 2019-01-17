package net.ajcloud.wansviewplusw.support.http.bean;

/**
 * Created by mamengchao on 2018/06/27.
 * Function:    直播信息
 */
public class LiveSrcBean {
    public String reqType;
    public String reqServer;            // 中继服务器, 针对reqType = 3,4
    public String token;                // 令牌, 可拼接 stream.wanUrl?token 或 stream.localUrl?token
    public String encrytMode;           // 内容是否加密: 0, 1
    public String sessionKey;           // 用于内容加密的会话密钥
    public StreamInfoBean.StreamInfo stream;
}
