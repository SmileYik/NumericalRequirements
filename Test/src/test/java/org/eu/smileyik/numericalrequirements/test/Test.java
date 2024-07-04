package org.eu.smileyik.numericalrequirements.test;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * 自动测试， 插件运行完毕后将会在主线程调用测试入口方法。
 */
public class Test {
    private static final class MyLogger {
        private final Logger l = Logger.getLogger("TEST");
        private final PrintStream bw;
        private boolean stopLogging = false;

        public MyLogger(File logFile) {
            try {
                bw = new PrintStream(new FileOutputStream(logFile)) {
                    @Override
                    public void print(String s) {
                        super.print(s);
                        if (!stopLogging) { l.info(s); }
                    }

                    @Override
                    public void println(String x) {
                        super.println(x);
                        // if (!stopLogging) logger.info(x);
                    }

                    @Override
                    public void println() {
                        super.println();
                        if (!stopLogging) l.info("\n");
                    }

                    @Override
                    public PrintStream printf(String format, Object... args) {
                        if (!stopLogging) l.info(String.format(format, args));
                        return super.printf(format, args);
                    }
                };
                System.setOut(bw);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public String spawnTime() {
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            return String.format(
                    "[%04d-%02d-%02d %02d:%02d:%02d] ",
                    instance.get(Calendar.YEAR),
                    instance.get(Calendar.MONTH) + 1,
                    instance.get(Calendar.DAY_OF_MONTH),
                    instance.get(Calendar.HOUR_OF_DAY),
                    instance.get(Calendar.MINUTE),
                    instance.get(Calendar.SECOND)
            );
        }

        private void writeTime() {
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            bw.print(spawnTime());
        }

        public synchronized void info(String message) {
            stopLogging = true;
            l.info(message);
            writeTime();
            bw.print("[INFO] ");
            bw.print(message);
            bw.println();
            stopLogging = false;
        }

        public synchronized void warning(String message) {
            stopLogging = true;
            l.warning(message);

            writeTime();
            bw.print("[WARNING] ");
            bw.print(message);
            bw.println();
            stopLogging = false;
        }

        public void close() throws IOException {
            bw.close();
        }
    }

    private final MyLogger logger = new MyLogger(
            new File(NumericalRequirements.getPlugin().getDataFolder(), "test.log")
    );

    public static void start() {
        PrintStream out = System.out;
        NumericalRequirements.getPlugin().getLogger().info("--------- Test Starting ---------");
        new Test().startTest();
        NumericalRequirements.getPlugin().getLogger().info("--------- Test Finished ---------");
        System.setOut(out);
    }

    public void startTest() {
        for (Class<?> aClass : getClasses()) {
            logger.info("========================== Class ============================");
            logger.info("        " + aClass.getSimpleName());
            logger.info("-------------------------------------------------------------");
            try {
                Constructor<?> newInstance = aClass.getDeclaredConstructor();
                for (Method method : getTestMethods(aClass)) {
                    method.setAccessible(true);
                    logger.info("++++++++++++++ Method ++++++++++++++++++");
                    logger.info("        " + method.getName());
                    logger.info("----------------------------------------");
                    try {
                        Object o = newInstance.newInstance();
                        long nanoTime = System.nanoTime();
                        method.invoke(o);
                        logger.info("Executing " + method.getName() + ": " + (System.nanoTime() - nanoTime) + " nanoseconds");
                    } catch (Exception e) {
                        logger.warning("Exception while executing " + method.getName() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                    logger.info("++++++++++++++++++++++++++++++++++++++++");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("============================================================");
        }
        try {
            logger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Method> getTestMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(NeedTest.class)) {
                methods.add(declaredMethod);
            }
        }
        return methods;
    }

    private List<Class<?>> getClasses() {
        File file = getPluginJarFile();
        List<Class<?>> classes = new ArrayList<>();
        if (file == null) {
            return classes;
        }
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName().replace('/', '.');
                if (name.startsWith(getClass().getPackage().getName()) && name.endsWith(".class")) {
                    name = name.substring(0, name.length() - 6);
                    try {
                        Class<?> clazz = Class.forName(name);
                        if (clazz.isAnnotationPresent(NeedTest.class)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private File getPluginJarFile() {
        File plugins = NumericalRequirements.getPlugin().getDataFolder().getParentFile();
        File[] jars = plugins.listFiles();
        assert jars != null;
        for (File jar : jars) {
            if (jar.isDirectory() || !jar.getName().toLowerCase().endsWith(".jar")) {
                continue;
            }
            try {
                PluginDescriptionFile des = NumericalRequirements.getPlugin().getPluginLoader().getPluginDescription(jar);
                if (des.getName().equalsIgnoreCase("NumericalRequirements")) {
                    return jar;
                }
            } catch (InvalidDescriptionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
