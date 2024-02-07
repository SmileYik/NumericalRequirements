package org.eu.smileyik.numericalrequirements.core.event;

import org.bukkit.event.HandlerList;

public class Event extends org.bukkit.event.Event {
    public Event() {
    }

    public Event(boolean isAsync) {
        super(isAsync);
    }

    private static final HandlerList handlerList = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
