package org.eu.smileyik.numericalrequirements.debug;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class DebugLogger implements Closeable {
    private static DebugLogger instance;

    private final Logger logger;
    private final BufferedWriter logWriter;

    public DebugLogger(Logger logger, File debugFile) throws IOException {
        if (instance != null) {
            instance.close();
        }

        this.logger = logger;
        this.logWriter = new BufferedWriter(new FileWriter(debugFile));
        instance = this;
    }

    private static String getTime() {
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

    public static void debug(Throwable obj) {
        try(StringWriter stringWriter = new StringWriter();) {
            obj.printStackTrace(new PrintWriter(stringWriter));
            stringWriter.flush();
            String string = stringWriter.toString();
            debug(Thread.currentThread().getStackTrace()[2],"%s", string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void debug(Object obj) {
        debug(Thread.currentThread().getStackTrace()[2],"%s", Objects.toString(obj));
    }

    public static void debug(String message, Object... args) {
        debug(Thread.currentThread().getStackTrace()[2], message, args);
    }

    public static void debug(StackTraceElement element, String message, Object... args) {
        if (instance == null) {
            return;
        }

        try {
            String format = String.format(message, args);
            String method = String.format(
                    "[%s#%s] ",
                    element.getClassName(),
                    element.getMethodName()
            );
            instance.logWriter.write(getTime());
            instance.logWriter.write(method);
            instance.logWriter.write(format);
            instance.logWriter.newLine();
            instance.logger.info(method + format);
            instance.logWriter.flush();
        } catch (IOException ignore) {

        }
    }

    public static void debug(DebugFunction function) {
        if (instance != null) {
            function.apply(Thread.currentThread().getStackTrace()[2]);
        }
    }

    public static void debugTime(Function function) {
        long nanos = System.nanoTime();
        function.apply();
        nanos = System.nanoTime() - nanos;
        debug(Thread.currentThread().getStackTrace()[2], "run this function spends %fms", nanos / 1.0E6);
    }

    public static StackTraceElement getStackTraceElement(int deep) {
        return Thread.currentThread().getStackTrace()[deep];
    }

    @Override
    public void close() throws IOException {
        logWriter.flush();
        logWriter.close();
    }

    public static DebugLogger getInstance() {
        return instance;
    }

    public interface Function {
        void apply();
    }

    public interface DebugFunction {
        void apply(StackTraceElement e);
    }
}
