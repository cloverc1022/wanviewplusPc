package net.ajcloud.wansviewplusw.support.eventbus.event;

import net.ajcloud.wansviewplusw.support.eventbus.Event;
import net.ajcloud.wansviewplusw.support.eventbus.EventType;

public class ChangeTabEvent extends Event {
    @Override
    public EventType getType() {
        return EventType.CHANGE_TAB;
    }
}
