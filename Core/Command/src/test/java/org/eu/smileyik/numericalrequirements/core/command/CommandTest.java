package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.junit.Test;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class CommandTest {

    @Command(value = "cmd", aliases = "a")
    public static class RootCommand {

    }

    @Command(value = "sub1", parentCommand = "cmd")
    public static class SubCommand1 {
        @Command("abc")
        public void abc(CommandSender sender, String[] args) {
            sender.sendMessage(Arrays.toString(args));
        }
    }

    @Command(value = "sub2", parentCommand = "cmd")
    public static class SubCommand2 {
        @Command(value = "abc", args = {"player", "number"})
        public void abc(CommandSender sender, String[] args) {
            sender.sendMessage(Arrays.toString(args));
        }

        @Command(value = "abc", args = {"player"})
        public boolean abc2(CommandSender sender, String[] args) {
            sender.sendMessage(Arrays.toString(args));
            return true;
        }
    }

    CommandSender sender = new CommandSender() {
        @Override
        public void sendMessage(String s) {
            System.out.println(s);
        }

        @Override
        public void sendMessage(String[] strings) {

        }

        @Override
        public Server getServer() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean isPermissionSet(String s) {
            return false;
        }

        @Override
        public boolean isPermissionSet(Permission permission) {
            return false;
        }

        @Override
        public boolean hasPermission(String s) {
            return false;
        }

        @Override
        public boolean hasPermission(Permission permission) {
            return false;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, int i) {
            return null;
        }

        @Override
        public void removeAttachment(PermissionAttachment permissionAttachment) {

        }

        @Override
        public void recalculatePermissions() {

        }

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            return null;
        }

        @Override
        public boolean isOp() {
            return false;
        }

        @Override
        public void setOp(boolean b) {

        }
    };

    @Test
    public void test() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvalidClassException {
        CommandService service = new CommandService((key, objs) -> key, new CommandMessageFormat() {
            @Override
            public String notFound() {
                return "";
            }

            @Override
            public String notFound(String suggestCommandHelp) {
                return suggestCommandHelp;
            }

            @Override
            public String commandError() {
                return "";
            }

            @Override
            public String commandError(String suggestCommandHelp) {
                return suggestCommandHelp;
            }

            @Override
            public String notPlayer() {
                return "";
            }

            @Override
            public String notPermission() {
                return "";
            }
        }, RootCommand.class, SubCommand1.class, SubCommand2.class);
        String[] cmds = new String[] {
                "a help",
                "abc help",
                "cmd help",
                "cmd help2",
                "cmd sub1",
                "cmd sub2 help",
                "cmd sub2 abc",
                "cmd sub1 abc",
                "cmd sub1 abc help",
                "cmd sub1 abc def",
                "cmd sub2 abc def",
                "cmd sub2 abc def igh",
                "cmd sub3 abc def igh"
        };
        for (String cmd : cmds) {
            System.out.println(cmd + ": ");
            String[] s = cmd.split(" ");
            service.execute(sender, s[0], Arrays.copyOfRange(s, 1, s.length));
        }
    }
}
