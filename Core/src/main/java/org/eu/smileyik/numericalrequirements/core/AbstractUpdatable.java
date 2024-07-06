package org.eu.smileyik.numericalrequirements.core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractUpdatable implements Updatable {
    protected long lastNanoTimestamp;
    private boolean firstRun = true;
    private final Lock lock = new ReentrantLock();

    @Override
    public final boolean update() {
        if (lock.tryLock()) {
            try {
                if (!firstRun) {
                    if (System.nanoTime() - lastNanoTimestamp > period() * 1000000L) {
                        boolean result = doUpdate((System.nanoTime() - lastNanoTimestamp) / 1000000000D);
                        lastNanoTimestamp = System.nanoTime();
                        return result;
                    }
                } else {
                    firstRun = false;
                    lastNanoTimestamp = System.nanoTime();
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    protected abstract boolean doUpdate(double second);
}
