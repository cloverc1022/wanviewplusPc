package net.ajcloud.wansviewplusw.support.http.bean.device;

/**
 * Created by mamengchao on 2018/12/17.
 * Function:
 */
public class ConDeviceBean {
    public String deviceId;
    public String aliasName;    //aliasName
    public int deviceType;      //设备类型，1-普通摄像头，2-门锁，3-电池摄像头，4-可视门铃，8-网关
    public int conStatus;         //当前的关联状态，0-无效，1-有效
    public int conType;          //和账户的关联类型，0-绑定，1-分享，2-子设备
    public String  conTs;          //创建关联的时间戳
}
