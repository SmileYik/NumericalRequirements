package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;
import org.eu.smileyik.numericalrequirements.core.command.exception.NoRootCommandException;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandService implements CommandExecutor, TabExecutor {
    private final Map<String, CommandMethod> commandMethodMap = new HashMap<>();
    private final Map<String, TabSuggest> tabSuggestMap = new HashMap<>();
    private final List<DefaultTabSuggest> defaultTabSuggests = new LinkedList<>();
    private final CommandTranslator translator;
    private final CommandMessageFormat format;

    public CommandService(CommandTranslator translator, CommandMessageFormat format, Class<?> ... classes) throws InvalidClassException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.translator = translator;
        this.format = format;
        registerCommands(classes);
    }

    public void registerToBukkit(Plugin plugin) {
        commandMethodMap.forEach((s, commandMethod) -> {
            plugin.getServer().getPluginCommand(commandMethod.getCommand()).setExecutor(this);
            plugin.getServer().getPluginCommand(commandMethod.getCommand()).setTabCompleter(this);
        });
    }

    private void registerCommands(Class<?> ... classes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, InvalidClassException {
        Map<String, CommandMethod> map = new HashMap<>();
        List<String> rootCommands = new ArrayList<>();
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(Command.class)) {
                throw new InvalidClassException("It's not 'Command' annotation present: " + clazz);
            }
            Command command = clazz.getAnnotation(Command.class);
            CommandI18N commandI18N = null;
            if (clazz.isAnnotationPresent(CommandI18N.class)) {
                commandI18N = clazz.getAnnotation(CommandI18N.class);
            }
            Object instance = clazz.getConstructor().newInstance();
            CommandMethod commandMethod = new CommandMethod(command, commandI18N, instance, null);
            map.put(command.value(), commandMethod);
            if (command.parentCommand().isEmpty()) {
                rootCommands.add(command.value());
            }

            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (!method.isAnnotationPresent(Command.class)) {
                    continue;
                }
                Command sub = method.getAnnotation(Command.class);
                CommandI18N subCommandI18N = null;
                if (method.isAnnotationPresent(CommandI18N.class)) {
                    subCommandI18N = method.getAnnotation(CommandI18N.class);
                }
                commandMethod.addChild(new CommandMethod(sub, subCommandI18N, instance, method));
            }
        }

        if (rootCommands.isEmpty()) {
            throw new NoRootCommandException();
        }

        map.forEach((key, cmd) -> {
            if (cmd.hasParentCommand()) {
                CommandMethod commandMethod = map.get(cmd.getParentCommand());
                if (commandMethod == null) {
                    throw new RuntimeException(key + " need parent command: " + cmd.getParentCommand());
                }
                commandMethod.addChild(cmd);
            }
        });

        for (String root : rootCommands) {
            CommandMethod cmd = map.get(root);
            commandMethodMap.put(root.toLowerCase(), cmd);
            cmd.spawnHelp(translator);
            for (String alias : cmd.getAliases()) {
                commandMethodMap.put(alias.toLowerCase(), cmd);
            }
        }
        map.clear();
    }

    public void execute(CommandSender sender, String label, String[] args) {
        try {
            Result result = doExecute(sender, label, args);
            switch (result.getResult()) {
                case Succeed:
                    break;
                case NotFound:
                    if (result.getSuggestion() == null) {
                        sender.sendMessage(format.notFound());
                    } else {
                        CommandMethod suggestion = result.getSuggestion();
                        StringBuilder sb = new StringBuilder();
                        while (suggestion != null) {
                            sb.append('\n').append(suggestion.getHelp(sender, label));
                            suggestion = suggestion.getNext();
                        }
                        sender.sendMessage(format.notFound(sb.substring(1)));
                    }
                    break;
                case CommandError:
                    if (result.getSuggestion() == null) {
                        sender.sendMessage(format.commandError());
                    } else {
                        CommandMethod suggestion = result.getSuggestion();
                        StringBuilder sb = new StringBuilder();
                        while (suggestion != null) {
                            sb.append('\n').append(suggestion.getHelp(sender, label));
                            suggestion = suggestion.getNext();
                        }
                        sender.sendMessage(format.commandError(sb.substring(1)));
                    }
                    break;
                case NotPlayer:
                    sender.sendMessage(format.notPlayer());
                    break;
                case NoPermission:
                    sender.sendMessage(format.notPermission());
                    break;
            }

        } catch (InvocationTargetException | IllegalAccessException e) {
            sender.sendMessage(format.commandError());
            e.printStackTrace();
        }
    }

    public void registerTabSuggest(TabSuggest tabSuggest) {
        if (tabSuggest instanceof DefaultTabSuggest) {
            defaultTabSuggests.add((DefaultTabSuggest) tabSuggest);
            return;
        }
        tabSuggestMap.put(tabSuggest.getKeyword().toLowerCase(), tabSuggest);
    }

    public void shutdown() {
        for (CommandMethod value : commandMethodMap.values()) {
            value.clear();
        }
        tabSuggestMap.clear();
        commandMethodMap.clear();
        defaultTabSuggests.clear();
    }

    private Result doExecute(CommandSender sender, String label, String[] args) throws InvocationTargetException, IllegalAccessException {
        CommandMethod commandMethod = commandMethodMap.get(label.toLowerCase());
        if (commandMethod != null) {
            if (args.length == 0) return new Result(ExecuteResult.CommandError, commandMethod);
            if (!commandMethod.hasPermission(sender)) {
                return Result.RESULT_NO_PERMISSION;
            }
            return commandMethod.execute(sender, label, args, 0);
        }
        return Result.RESULT_NOT_FIND;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        execute(commandSender, s, strings);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {
        if (!commandMethodMap.containsKey(s)) {
            return Collections.emptyList();
        }
        CommandMethod commandMethod = commandMethodMap.get(s);
        if (!commandMethod.hasPermission(sender)) {
            return Collections.emptyList();
        }
        return commandMethod.getTabSuggest(defaultTabSuggests, tabSuggestMap, sender, strings, 0, 0);
    }
}
