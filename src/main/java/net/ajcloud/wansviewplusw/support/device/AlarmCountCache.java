package net.ajcloud.wansviewplusw.support.device;

import net.ajcloud.wansviewplusw.support.http.bean.AlarmBean;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mamengchao on 2018/07/11.
 * Function:未读报警数量缓存
 */
public class AlarmCountCache {

    /**
     * 未读报警，每个设备对应最新一条告警的时间 <did,boolean>
     */
    private Map<String, Boolean> alarmUnReadMap = new HashMap<>();

    public AlarmCountCache() {
    }

    /**
     * 设置最新报警时间
     */
    public synchronized void setAlarmTime(List<AlarmBean> alarms) {
        if (alarms != null) {
            for (AlarmBean bean : alarms) {
                String ats = bean.ats;
                if (StringUtil.isNullOrEmpty(ats) || StringUtil.equals(ats, "0")) {
                    alarmUnReadMap.put(bean.did, true);
                } else {
                    alarmUnReadMap.put(bean.did, false);
                }
            }
        }
    }

    /**
     * 设置某个设备的未读消息
     */
    public synchronized void setDeviceUnread(String deviceId, boolean isRead) {
        alarmUnReadMap.put(deviceId, isRead);
//        EventBus.getDefault().post(new AlarmUnreadEvent());
    }

    /**
     * 清除所以设备的未读消息
     */
    public void clear() {
        alarmUnReadMap.clear();
    }

    /**
     * 获取某个设备是否有未读消息
     */
    public boolean hasUnread(String deviceId) {
        return alarmUnReadMap.get(deviceId);
    }
}
