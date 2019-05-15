package net.ajcloud.wansviewplusw.support.http.bean;

import net.ajcloud.wansviewplusw.support.utils.play.TcprelayHelper;

/**
 * Created by mamengchao on 2019/03/08.
 * Function:    p2p/relay链接信息
 */
public class LinkInfo {

    private String deviceId;                //设备Id
    private int num;                            //连接号，当前连接标识，用于断开连接等
    private int quality;                        //当前连接视频质量
    private String serverIp;                 // 中继服务器
    private int port;                            //端口号
    private String localUrl;                  //接口返回url，用于生成url
    private String url;                         //最终的播放url
    private int status;                         //当前连接状态 0：正在连接     1：成功    2：失败
    private int connectType;                         // connect_type == 0 ? "P2P":"RLY"
    private boolean isValid;                         //是否有效
    private TcprelayHelper.ConnectCallback connectCallback;

    public LinkInfo(String deviceId, int quality, TcprelayHelper.ConnectCallback connectCallback) {
        this.deviceId = deviceId;
        this.quality = quality;
        this.status = 0;
        this.connectType = 1;
        this.connectCallback = connectCallback;
        this.isValid = true;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public void resetQuality(int quality, TcprelayHelper.ConnectCallback connectCallback) {
        this.quality = quality;
        this.status = 0;
        this.num = 0;
        this.serverIp = "";
        this.port = 0;
        this.localUrl = "";
        this.url = "";
        this.connectType = 1;
        this.connectCallback = connectCallback;
    }

    public synchronized TcprelayHelper.ConnectCallback getConnectCallback() {
        return connectCallback;
    }

    public synchronized void setConnectCallback(TcprelayHelper.ConnectCallback connectCallback) {
        this.connectCallback = connectCallback;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
