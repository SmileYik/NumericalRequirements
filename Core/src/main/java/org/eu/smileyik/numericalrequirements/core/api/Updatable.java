package org.eu.smileyik.numericalrequirements.core.api;

public interface Updatable {
    boolean update();

    /**
     * update()方法后，隔多久ms间隔再执行一次.
     * @return
     */
    long period();
}
