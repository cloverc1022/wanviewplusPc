package net.ajcloud.wansviewplusw.support.http.bean.cloudStoragePlan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mamengchao on 2018/08/01.
 * Function: 云存储订阅列表
 */
public class CloudStorageOrderBean implements Serializable{

    public List<CloudStorageOrder> orders;
    public List<String> relDids;

    public static class CloudStorageOrder implements Serializable{
        public Produce product;
        public BuyerBean buyer;
        public String limitDevices;
        public String alarmVideo;
        public String limitFullDayDevices;
        public String status;
        public String _id;
        public String sku;
        public String level;
        public String cycleDays;
        public String payMode;
        public String trialPeriod;      //1:试用  0：非试用
        public List<CloudStorageDeviceBean> devices;
        public long validTsStart;
        public long validTsEnd;
    }
}
