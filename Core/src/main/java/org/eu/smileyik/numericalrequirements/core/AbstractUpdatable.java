package org.eu.smileyik.numericalrequirements.core;

public abstract class AbstractUpdatable implements Updatable {
    protected long lastNanoTimestamp;
    private boolean firstRun = true;

    @Override
    public synchronized final boolean update() {
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
        return false;
    }

    protected abstract boolean doUpdate(double second);
}
