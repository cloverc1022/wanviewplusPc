package net.ajcloud.wansviewplusw.support.eventbus.event;

import net.ajcloud.wansviewplusw.support.eventbus.Event;
import net.ajcloud.wansviewplusw.support.eventbus.EventType;

public class DeviceRefreshEvent extends Event {

    private String deviceId;

    public DeviceRefreshEvent(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public EventType getType() {
        return EventType.DEVICE_REFRESH;
    }
}
