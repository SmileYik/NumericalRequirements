package org.eu.smileyik.numericalrequirements.core.api.extension;

import java.util.Collection;

public interface ExtensionService {
    void loadExtensions();

    boolean register(Extension extension);
    Extension getExtensionById(String id);

    void registerTask(ExtensionTask task);

    ExtensionTask getTaskByTaskId(String id);

    Collection<ExtensionTask> getRegisteredTasks();

    void unregisterAll();

    void unregister(Extension extension);

    void shutdown();
}
