package org.eu.smileyik.numericalrequirements.core.extension;

import org.eu.smileyik.numericalrequirements.core.extension.task.ExtensionTask;

import java.util.Collection;

public interface ExtensionService {
    void loadExtensions();

    boolean register(Extension extension);
    Extension getExtensionById(String id);

    void registerTask(ExtensionTask task);

    ExtensionTask getTaskByTaskId(String id);

    Collection<ExtensionTask> getRegisteredTasks();

    void unregisterAll();

    void shutdown();
}
