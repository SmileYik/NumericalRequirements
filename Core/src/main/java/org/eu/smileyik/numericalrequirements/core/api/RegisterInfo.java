package org.eu.smileyik.numericalrequirements.core.api;

public interface RegisterInfo {
    String getId();
    String getName();
    String getAuthor();
    String getVersion();
    default String getDescription() {
        return "No Description";
    }
    void onRegister();

    void onUnregister();

    boolean isDisable();
}
