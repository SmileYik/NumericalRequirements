package org.eu.smileyik.numericalrequirements.core.api;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractUpdatable implements Updatable {
    protected long lastNanoTimestamp;
    private boolean firstRun = true;
    private final Lock lock = new ReentrantLock();
    private double seconds = 0;

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

    @Override
    public boolean update(double seconds) {
        lock.lock();
        try {
            double p = period() / 1000D;
            this.seconds += seconds;
            if (this.seconds >= p) {
                boolean flag = doUpdate(this.seconds);
                this.seconds = Math.min(0, seconds - p);
                return flag;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    protected void resetTimestamp() {
        this.lastNanoTimestamp = System.nanoTime();
    }

    protected abstract boolean doUpdate(double second);
}
