package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:
 */
public class SoundMonitorBean implements Serializable {
    public int enable;
    public int susceptiveness;
    public List<Policy> policies;

    public static class Policy implements Serializable{
        private int no;
        private int enable;
        private int respondMode;
        private String startTime;
        private String endTime;
        private List<Integer> weekDays;
    }
}
