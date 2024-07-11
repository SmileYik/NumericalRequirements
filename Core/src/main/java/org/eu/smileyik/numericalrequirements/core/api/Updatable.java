package org.eu.smileyik.numericalrequirements.core.api;

public interface Updatable extends Runnable {

    boolean update();

    default boolean update(double seconds) {
        return update();
    }

    /**
     * update()方法后，隔多久ms间隔再执行一次.
     * @return
     */
    long period();

    @Override
    default void run() {
        update();
    }
}
