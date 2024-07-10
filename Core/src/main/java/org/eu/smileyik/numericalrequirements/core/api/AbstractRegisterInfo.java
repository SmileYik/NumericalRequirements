package org.eu.smileyik.numericalrequirements.core.api;

public abstract class AbstractRegisterInfo implements RegisterInfo {
    private final String id;
    private final String name;
    private final String author;
    private final String version;
    private final String description;
    private boolean disable = false;

    public AbstractRegisterInfo(String id, String name, String author, String version, String description) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.version = version;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public final void onRegister() {
        disable = false;
        register();
    }

    protected abstract void register();

    @Override
    public final void onUnregister() {
        disable = true;
        unregister();
    }

    protected abstract void unregister();

    @Override
    public boolean isDisable() {
        return disable;
    }
}
