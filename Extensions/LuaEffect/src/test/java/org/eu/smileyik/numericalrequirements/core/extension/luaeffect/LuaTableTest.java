package org.eu.smileyik.numericalrequirements.core.extension.luaeffect;

import org.eu.smileyik.numericalrequirements.luaeffect.LuaConfigTool;
import org.junit.Test;
import org.keplerproject.luajava.LuaException;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import tk.smileyik.luainminecraftbukkit.api.luaconfig.LuaConfig;
import tk.smileyik.luainminecraftbukkit.api.luaconfig.LuaConfigEntity;
import tk.smileyik.luainminecraftbukkit.util.luahelper.LuaHelper;
import tk.smileyik.luainminecraftbukkit.util.nativeloader.NativeLuaLoader;
import tk.smileyik.luainminecraftbukkit.util.nativeloader.NativeVersion;

import java.io.*;
import java.util.Arrays;

public class LuaTableTest {

    public static class LuaTableTestEntity extends LuaHelper implements LuaConfigEntity {
        public static Class<?> tool = LuaConfigTool.class;

        private LuaConfig config;

        private Object callback;

        private Object table;

        public Object printJavaStringArray;

        public Object[] objects;

        public void setCallback(Object callback) {
            this.callback = callback;
        }

        public void setTable(Object table) {
            System.out.println(table == this.table);
            this.table = table;
        }

        public Object getTable() {
            return table;
        }

        @Override
        public LuaConfig getLuaConfig() {
            return config;
        }

        @Override
        public void setLuaConfig(LuaConfig config) {
            this.config = config;
        }

        /**
         * 将Lua中的表(数组)转换为java中的Object类型数组.
         * @param table 表（数组）
         * @return
         */
        public static Object[] toObjectArray(LuaTable table) throws LuaException {
            int size = table.length();
            Object[] ans = new Object[size];
            for (int i = 1; i <= size; ++i) {
                ans[i - 1] = CoerceLuaToJava.coerce(table.get(i), Object.class);
            }
            return ans;
        }

        @Override
        public String toString() {
            return "LuaTableTestEntity{" +
                    ", callback=" + callback +
                    ", table=" + table +
                    ", printJavaStringArray=" + printJavaStringArray +
                    ", objects=" + Arrays.toString(objects) +
                    '}';
        }
    }

    File lib = new File("./src/test/resources/");

    @Test
    public void testInside() throws Exception {
        System.out.println(new File(".").getCanonicalFile());
        try (
                InputStream resourceAsStream = LuaTableTest.class.getResourceAsStream("/LuaTableTest.lua");
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))
        ) {
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append("\n").append(line);
            }
            LuaTableTestEntity entity = new LuaTableTestEntity();
            LuaConfig config = LuaConfig.loadInsideLuaConfig(sb.toString())
                    .addGlobal("entity", entity);
            config.config();
            config.callClosureReturnObject(entity.callback, entity.table, 1, 2, 3, 4);
            config.callClosureReturnObject(entity.printJavaStringArray, (Object) new String[] {"123", "456"});
            config.close();
            System.out.println(Arrays.toString(entity.objects));
        }

    }

    @Test
    public void testOutsideLua5_4() throws Exception {
        try (
                InputStream resourceAsStream = LuaTableTest.class.getResourceAsStream("/LuaTableTest.lua");
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))
        ) {
            NativeLuaLoader.initNativeLua(lib, NativeVersion.LUA_5_4);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append("\n").append(line);
            }
            LuaTableTestEntity entity = new LuaTableTestEntity();
            LuaConfig config = LuaConfig.loadOutsideLuaConfig(sb.toString())
                    .addGlobal("entity", entity);
            config.config();
            config.callClosureReturnObject(entity.callback, entity.table);
            config.callClosureReturnObject(entity.printJavaStringArray, (Object) new String[] {"123", "456"});
            config.close();
            System.out.println(Arrays.toString(entity.objects));
        }
    }

    @Test
    public void testOutsideLuaJit() throws IOException, LuaException {
        try (
                InputStream resourceAsStream = LuaTableTest.class.getResourceAsStream("/LuaTableTest.lua");
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))
        ) {
            NativeLuaLoader.initNativeLua(lib, NativeVersion.LUAJIT_2_1_0_BETA3);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append("\n").append(line);
            }
            LuaTableTestEntity entity = new LuaTableTestEntity();
            LuaConfig config = LuaConfig.loadOutsideLuaConfig(sb.toString())
                    .addGlobal("entity", entity);
            config.config();
            config.callClosureReturnObject(entity.callback, entity.table);
            config.callClosureReturnObject(entity.printJavaStringArray, (Object) new String[] {"123", "456"});
            config.close();
            System.out.println(Arrays.toString(entity.objects));
        }
    }
}
