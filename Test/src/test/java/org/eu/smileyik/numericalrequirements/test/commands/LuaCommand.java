package org.eu.smileyik.numericalrequirements.test.commands;

import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.test.TestCommand;
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
}
