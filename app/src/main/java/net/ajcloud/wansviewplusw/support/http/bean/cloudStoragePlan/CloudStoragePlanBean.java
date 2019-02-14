package net.ajcloud.wansviewplusw.support.http.bean.cloudStoragePlan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mamengchao on 2018/07/20.
 * Function:
 */
public class CloudStoragePlanBean implements Serializable {

    public List<CloudStoragePlan> plans;
    public TrialBean trial;

    public static class CloudStoragePlan implements Serializable{
        public java.lang.Package monthly;
        public java.lang.Package quarterly;
        public java.lang.Package yearly;
        public List<String> payModes;
        public String trialDays;
        public String limitDevices;
        public String alarmVideo;
        public String limitFullDayDevices;
        public String status;
        public String _id;
        public String sku;
        public String level;
        public String cycleDays;
        public String name;
    }
}
