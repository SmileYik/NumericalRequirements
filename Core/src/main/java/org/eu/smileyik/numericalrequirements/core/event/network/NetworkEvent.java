package org.eu.smileyik.numericalrequirements.core.event.network;

import org.eu.smileyik.numericalrequirements.core.event.Event;

/**
 * 网络事件, 所有网络事件都是异步事件.
 */
public abstract class NetworkEvent extends Event {
    public NetworkEvent() {
        super(true);
    }
}
