package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/07/10.
 * Function:
 */
public class CloudStorPlanBean implements Serializable {
    public String sku;
    public String level;
    public String validTsStart;
    public String validTsEnd;
    public String keepDays;
    public String alarmVideo;
    public String fullDay;
    public String lastStoreTs;
    public String expireViewAt;
    public String orderId;
}
