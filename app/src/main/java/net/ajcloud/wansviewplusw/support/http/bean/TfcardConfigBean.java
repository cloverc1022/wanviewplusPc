package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/09/20.
 * Function:
 */
public class TfcardConfigBean implements Serializable {
    public int status;                                  // 值域: 1 - 无, 2 - 正常, 3 - 异常, 4 - 正在删除, 5 - 正在格式化
    public String label;                              //卷名 最长16字符
    public String serialNo;                         // 序列号
    public String manufacturerId;              // 制造商ID
    public String manufactureDate;           // 生产日期
    public String oemId;                            // OEM ID
    public String hwReversion;                  //硬件版本号
    public String fwReversion;                   // 固件版本号
    public long capacityBytes;                   // 设备字节容量
    public long freespaceBytes;                // 可用字节空间
}
