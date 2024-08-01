package org.eu.smileyik.numericalrequirements.test.commands;

import org.bukkit.scheduler.BukkitTask;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.test.TestCommand;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaObject;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;
import tk.smileyik.luainminecraftbukkit.util.luahelper.LuaHelper;

import java.nio.charset.StandardCharsets;

@TestCommand("lua")
public class LuaCommand {
    private LuaState luaState;

    public void run(String[] args) {
        String str = String.join(" ", args);
        if (str.equals("init console") && luaState == null) {
            luaState = LuaStateFactory.newLuaState();
            luaState.openLibs();
            luaState.pushJavaObject(DebugLogger.class);
            luaState.setGlobal("debug");
            luaState.pushJavaObject(LuaHelper.class);
            luaState.setGlobal("helper");
            luaState.pushJavaObject(NumericalRequirements.getInstance());
            luaState.setGlobal("nr");
            luaState.pushJavaObject(new Task(luaState));
            luaState.setGlobal("task");
            System.out.println("Lua Console Started");
            return;
        } else if (str.equals("close console")) {
            luaState.close();
            luaState = null;
            System.out.println("Lua Console Closed");
            return;
        }
        int ret = luaState.LloadBuffer(str.getBytes(StandardCharsets.UTF_8), "debug console");
        if (ret == 0) {
            ret = luaState.pcall(0, 0, 0);
        }
        if (ret != 0) {
            System.out.println("Run " + str + " failed: " + luaState.toString(-1));
        }
    }

    public static class Task {
        private final LuaState luaState;

        public Task(LuaState luaState) {
            this.luaState = luaState;
        }

        public BukkitTask runTask(Object object) {
            return NumericalRequirements.getPlugin().getServer().getScheduler().runTask(NumericalRequirements.getPlugin(), () -> {
                if (object instanceof LuaObject) {
                    try {
                        ((LuaObject) object).call(new Object[] {});
                    } catch (LuaException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
