package net.ajcloud.wansviewplusw.support.eventbus.event;

import net.ajcloud.wansviewplusw.support.eventbus.Event;
import net.ajcloud.wansviewplusw.support.eventbus.EventType;

public class MainStageCloseEvent extends Event {


    public MainStageCloseEvent() {

    }

    @Override
    public EventType getType() {
        return EventType.MAIN_STAGE_CLOSE;
    }
}
