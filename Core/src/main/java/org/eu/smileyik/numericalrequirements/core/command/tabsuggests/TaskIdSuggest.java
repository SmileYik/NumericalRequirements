package org.eu.smileyik.numericalrequirements.core.command.tabsuggests;

import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionService;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.core.command.TabSuggest;

import java.util.List;
import java.util.stream.Collectors;

public class TaskIdSuggest implements TabSuggest {
    private final ExtensionService extensionService;

    public TaskIdSuggest(ExtensionService extensionService) {
        this.extensionService = extensionService;
    }


    @Override
    public String getKeyword() {
        return "task";
    }

    @Override
    public List<String> suggest() {
        return extensionService.getRegisteredTasks()
                .stream()
                .map(ExtensionTask::getId)
                .collect(Collectors.toList());
    }
}
