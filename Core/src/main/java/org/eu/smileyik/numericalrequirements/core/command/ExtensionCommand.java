package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.command.CommandSender;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionService;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@CommandI18N("command")
@Command(
        value = "extension",
        colorCode = "color",
        parentCommand = "NumericalRequirements",
        permission = "NumericalRequirements.Admin"
)
public class ExtensionCommand {
    final ExtensionService extensionService = NumericalRequirements.getInstance().getExtensionService();

    @CommandI18N("command.extension")
    @Command(
            value = "run",
            colorCode = "color",
            args = {"task", "values"},
            isUnlimitedArgs = true
    )
    public void runTask(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(I18N.trp("command", "command.extension.error.no-valid-args"));
            return;
        }
        ExtensionTask task = extensionService.getTaskByTaskId(args[0]);
        if (task == null) {
            sender.sendMessage(I18N.trp("command", "command.extension.error.not-found-task", args[0]));
            return;
        }
        task.run(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @CommandI18N("command.extension")
    @Command(
            value = "taskInfo",
            colorCode = "color",
            args = {"task"}
    )
    public void taskInfo(CommandSender sender, String[] args) {
        ExtensionTask task = extensionService.getTaskByTaskId(args[0]);
        if (task == null) {
            sender.sendMessage(I18N.trp("command", "command.extension.error.not-found-task", args[0]));
            return;
        }
        sender.sendMessage(I18N.trp("command",
                "command.extension.taskInfo.info",
                task.getId(), task.getName(), task.getDescription(),
                task.getExtension().getInfo().getId(),
                task.getExtension().getInfo().getName()
        ));
    }

    @CommandI18N("command.extension")
    @Command(
            value = "tasks",
            colorCode = "color",
            args = { "page" },
            isUnlimitedArgs = true
    )
    public void tasks(CommandSender sender, String[] args) {
        int page = 1;
        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {

            }
        }
        Collection<ExtensionTask> registeredTasks = extensionService.getRegisteredTasks();
        Pair<Integer, Integer> pagePair = getPageNumber(registeredTasks, page, 5);
        Collection<ExtensionTask> list = getPage(registeredTasks, page, 5);

        StringBuilder sb = new StringBuilder();
        for (ExtensionTask task : list) {
            sb.append('\n').append(I18N.trp("command",
                    "command.extension.taskInfo.info",
                    task.getId(), task.getName(), task.getDescription(),
                    task.getExtension().getInfo().getId(),
                    task.getExtension().getInfo().getName()
            ));
        }
        sender.sendMessage(I18N.trp("command",
                "command.extension.tasks.list-page",
                pagePair.getFirst(), pagePair.getSecond(),
                sb.length() > 0 ? sb.substring(1) : ""
        ));
    }

    private Pair<Integer, Integer> getPageNumber(Collection<?> list, int page, int count) {
        int size = list.size();
        page = Math.max(page, 1);
        while ((page - 1) * count > size) {
            --page;
        }
        int end = size / count;
        if (size % count != 0) {
            end++;
        }
        return Pair.newUnchangablePair(page, end);
    }

    private <T> Collection<T> getPage(Collection<T> list, int page, int count) {
        int size = list.size();
        page = Math.max(page, 1);
        while ((page - 1) * count > size) {
            --page;
        }
        int start = (page - 1) * count;
        int end = page * count;
        return new ArrayList<>(list).subList(
            start, Math.min(end, size)
        );
    }
}
