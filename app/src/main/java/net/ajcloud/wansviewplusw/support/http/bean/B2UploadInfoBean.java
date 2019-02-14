package net.ajcloud.wansviewplusw.support.http.bean;

/**
 * Created by mamengchao on 2018/06/25.
 * Function:    b2上传信息
 */
public class B2UploadInfoBean {

    public String resourceType;
    public String resourceId;
    public String storageMode;
    public String context;                  //暂时为空
    public String uploadToken;              // 上传token
    public String uploadUrl;                // 上传地址
    public String uploadTokenExpire;        // 秒, 一般 22*60*60
    public int viewAngle;        //可用视角编号[1-4], cam-viewangle
}
