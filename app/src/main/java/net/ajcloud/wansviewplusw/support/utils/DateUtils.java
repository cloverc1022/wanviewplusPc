package net.ajcloud.wansviewplusw.support.utils;

public class DateUtils {


    public static String timeFormat(long seconds) {
        StringBuilder time = new StringBuilder();
        long hour = seconds % (24 * 60 * 60) / (60 * 60);
        long min = seconds % (24 * 60 * 60) % (60 * 60) / 60;
        long sec = seconds % (24 * 60 * 60) % (60 * 60) % 60;
        if (hour < 10) {
            time.append("0");
        }
        time.append(hour);
        time.append(":");
        if (min < 10) {
            time.append("0");
        }
        time.append(min);
        time.append(":");
        if (sec < 10) {
            time.append("0");
        }
        time.append(sec);
        return time.toString();
    }
}
