package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CommandMethod {
    private final Command commandInfo;
    private final CommandI18N commandI18N;
    private final Object instance;
    private final Method method;
    private String permission = null;
    private Map<String, CommandMethod> children = null;
    private List<CommandMethod> sortedMethod;
    private CommandMethod parent = null;
    private CommandMethod next = null;
    private String helpCache = null;
    private Map<String, Map<Character, Integer>> subcommandMap;

    public CommandMethod(Command commandInfo, CommandI18N commandI18N, Object instance, Method method) {
        this.commandInfo = commandInfo;
        this.commandI18N = commandI18N;
        this.instance = instance;
        this.method = method;
        if (!commandInfo.permission().isEmpty()) {
            permission = commandInfo.permission();
        }
    }

    public boolean hasChild() {
        return children != null;
    }

    public boolean canExecute() {
        return method != null;
    }

    public boolean hasParentCommand() {
        return !commandInfo.parentCommand().isEmpty();
    }

    public String getParentCommand() {
        return commandInfo.parentCommand();
    }

    public boolean isRootCommand() {
        return parent == null;
    }

    public String getCommand() {
        return commandInfo.value();
    }

    public void clear() {
        if (hasChild()) {
            for (CommandMethod value : children.values()) {
                while (value != null) {
                    value.clear();
                    value = value.next;
                }
            }
            children.clear();
            sortedMethod.clear();
            subcommandMap.clear();
        }
        permission = null;
        parent = null;
        children = null;
        sortedMethod = null;
        next = null;
        helpCache = null;
        subcommandMap = null;
    }

    public void addChild(CommandMethod commandMethod) {
        if (children == null) {
            children = new HashMap<>();
            subcommandMap = new HashMap<>();
            sortedMethod = new ArrayList<>();
        }
        if (permission != null && commandMethod.permission == null) {
            commandMethod.permission = permission;
        }
        String subcommand = commandMethod.getCommand().toLowerCase();
        commandMethod.parent = this;
        commandMethod.next = children.get(subcommand);
        children.put(subcommand, commandMethod);
        Map<Character, Integer> map = new HashMap<>();
        for (char c : subcommand.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        subcommandMap.put(subcommand, map);
        sortedMethod.add(commandMethod);
    }

    private String getColor(CommandTranslator tr) {
        return commandI18N == null ?
                commandInfo.colorCode() :
                tr.tr(String.format("%s.%s.%s", commandI18N.value(), getCommand(), commandInfo.colorCode()));
    }

    private String getSameWeightCommand() {
        if (parent == null) {
            return getCommand();
        }
        if (parent.children == null) {
            return getCommand();
        }
        int max = parent.children.values().stream().max(Comparator.comparing(it -> it.getCommand().length())).get().getCommand().length();
        StringBuilder sb = new StringBuilder(getCommand());
        while (sb.length() != max) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public void spawnHelp(CommandTranslator translator) {
        LinkedList<CommandMethod> list = new LinkedList<>();
        CommandMethod p = parent;
        while (p != null) {
            list.addFirst(p);
            p = p.parent;
        }
        StringBuilder sb = new StringBuilder();
        if (!list.isEmpty()) {
            CommandMethod cmd = list.removeFirst();
            sb.append(" ")
                    .append(cmd.getColor(translator))
                    .append("<%:root_command>");
        }
        for (CommandMethod cmd : list) {
            sb.append(" ")
                    .append(cmd.getColor(translator))
                    .append(cmd.getCommand());
        }
        sb.append(" ")
                .append(getColor(translator))
                .append(getCommand());
        if (canExecute()) {
            for (String arg : commandInfo.args()) {
                sb.append(" ")
                        .append("&b[")
                        .append(commandI18N == null ? arg : translator.tr(String.format("%s.%s.%s", commandI18N.value(), getCommand(), arg)))
                        .append("&b]");
            }
        } else {
            sortedMethod.sort(
                    Comparator
                            .comparingInt((CommandMethod l) -> l.getCommand().length())
                            .thenComparing(CommandMethod::getCommand)
            );
        }
        sb.append(" &r- ")
                .append(commandI18N == null ? commandInfo.description() : translator.tr(String.format("%s.%s.%s", commandI18N.value(), getCommand(), commandInfo.description())));
        helpCache = ChatColor.translateAlternateColorCodes('&', sb.substring(1));
        if (children != null) {
            children.values().forEach(it -> {
                it.spawnHelp(translator);
            });
        }
    }

    public String getHelp(String alias) {
        if (canExecute()) {
            return helpCache == null ? null : helpCache.replace("<%:root_command>", alias);
        }

        if (children != null) {
            StringBuilder sb = new StringBuilder();
            sortedMethod.forEach(it -> {
                sb.append("\n").append(it.helpCache);
            });
            return sb.substring(1).replace("<%:root_command>", alias);
        }
        return helpCache == null ? null : helpCache.replace("<%:root_command>", alias);
    }

    public String[] getAliases() {
        return commandInfo.aliases();
    }

    public boolean hasPermission(CommandSender sender) {
        return !(permission != null && !sender.isOp() && !sender.hasPermission(permission));
    }

    public List<String> getTabSuggest(List<DefaultTabSuggest> defaultSuggests, Map<String, TabSuggest> tabSuggestMap, CommandSender sender, String[] args, int idx, int step) {
        if (!hasChild()) {
            if (!hasPermission(sender)) {
                return Collections.emptyList();
            }
            if (idx == args.length - 1) {
                TabSuggest tabSuggest = null;
                if (commandInfo.args().length > idx - step) {
                    tabSuggest = tabSuggestMap.get(commandInfo.args()[idx - step].toLowerCase());
                }
                if (tabSuggest == null) {
                    for (DefaultTabSuggest defaultSuggest : defaultSuggests) {
                        if (defaultSuggest.matches(args, step - 1)) {
                            tabSuggest = defaultSuggest;
                            break;
                        }
                    }
                }
                return tabSuggest == null ? Collections.emptyList() : tabSuggest.suggest(args, step - 1);
            }
            return getTabSuggest(defaultSuggests, tabSuggestMap, sender, args, idx + 1, step);
        }
        if (idx == args.length - 1) {
            String prefix = args[idx];
            return subcommandMap
                    .keySet()
                    .stream()
                    .filter(it -> it.startsWith(prefix) && children.get(it).hasPermission(sender))
                    .collect(Collectors.toList());
        }
        CommandMethod commandMethod = children.get(args[idx]);
        if (commandMethod == null || !commandMethod.hasPermission(sender)) {
            return Collections.emptyList();
        }
        return commandMethod.getTabSuggest(defaultSuggests, tabSuggestMap, sender, args, idx + 1, step + 1);
    }

    public Result execute(CommandSender sender, String label, String[] args, int idx) throws InvocationTargetException, IllegalAccessException {
        if (args.length == idx + 1 && args[idx].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',
                    String.format(
                            "&e------------&rHELP&e-----------------\n" +
                            "%s\n" +
                            "&e---------------------------------", getHelp(sender instanceof Player ? ("/" + label) : label)
                    )
            ));
            return Result.RESULT_SUCCEED;
        }
        if (canExecute()) {
            if (args.length - idx == commandInfo.args().length || commandInfo.isUnlimitedArgs()) {
                if (commandInfo.needPlayer() && !(sender instanceof Player)) {
                    return Result.RESULT_NOT_PLAYER;
                }
                if (permission != null && !sender.isOp() && !sender.hasPermission(permission)) {
                    return Result.RESULT_NO_PERMISSION;
                }
                String[] strings = Arrays.copyOfRange(args, idx, args.length);
                method.invoke(instance, sender, strings);
                return Result.RESULT_SUCCEED;
            }
            return Result.RESULT_WRONG_COMMAND;
        }
        if (args.length == idx) return Result.RESULT_WRONG_COMMAND;
        if (!hasChild()) return Result.RESULT_WRONG_COMMAND;
        CommandMethod cmd = children.get(args[idx].toLowerCase());
        Result result = Result.RESULT_NOT_FIND;
        while (cmd != null) {
            result = cmd.execute(sender, label, args, idx + 1);
            if (result == Result.RESULT_SUCCEED) {
                return result;
            }
            cmd = cmd.next;
        }
        if (result.getSuggestion() != null) {
            return result;
        }
        String suggestSubcommand = findSuggestSubcommand(sender, args[idx].toLowerCase());
        if (suggestSubcommand != null) {
            return new Result(result.getResult(), children.get(suggestSubcommand));
        }
        return result;
    }

    private String findSuggestSubcommand(CommandSender sender, String wrongCmd) {
        List<String> available = subcommandMap.keySet()
                .stream()
                .filter(it -> children.get(it).hasPermission(sender))
                .collect(Collectors.toList());
        List<String> targets = available.stream()
                .filter(it -> it.startsWith(wrongCmd))
                .collect(Collectors.toList());
        if (targets.isEmpty()) {
            targets.addAll(available);
        }
        Map<Character, Integer> map = new HashMap<>();
        for (char c : wrongCmd.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        int min = Integer.MAX_VALUE;
        String suggest = null;
        for (String targetCommand : targets) {
            Map<Character, Integer> target = new HashMap<>(subcommandMap.get(targetCommand));
            for (Map.Entry<Character, Integer> c : map.entrySet()) {
                target.put(c.getKey(), Math.abs(target.getOrDefault(c.getKey(), 0) - c.getValue()));
            }
            int i = 0;
            for (Integer value : target.values()) {
                i += value;
            }
            if (i < min) {
                suggest = targetCommand;
                min = i;
            }
        }
        return suggest;
    }

    public CommandMethod getNext() {
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandMethod that = (CommandMethod) o;
        return Objects.equals(commandInfo.value(), that.commandInfo.value());
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandInfo.value());
    }
}
