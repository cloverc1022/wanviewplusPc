package net.ajcloud.wansviewplusw.support.http.bean.advertise;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/08/08.
 * Function:    广告
 */
public class AdvertisementBean implements Serializable{
    public String title;
    public AdvertiseUrl androidAdvUrls;
    public AdvertiseUrl iosAdvUrls;
    public TittleBean titleStyle;
    public String clickUrl;
    public String clickUrlTitle;
    public String startDate;
    public String endDate;
    public String publishTime;
    public int durationSec;
    public long startTimestamp;
    public long endTimestamp;
}
