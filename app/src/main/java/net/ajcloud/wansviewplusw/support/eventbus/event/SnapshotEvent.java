package net.ajcloud.wansviewplusw.support.eventbus.event;

import net.ajcloud.wansviewplusw.support.eventbus.Event;
import net.ajcloud.wansviewplusw.support.eventbus.EventType;

public class SnapshotEvent extends Event {
    @Override
    public EventType getType() {
        return EventType.SNAPSHOT;
    }
}
