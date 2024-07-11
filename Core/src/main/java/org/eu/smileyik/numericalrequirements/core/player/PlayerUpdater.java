package org.eu.smileyik.numericalrequirements.core.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PlayerUpdater {

    private final ThreadPoolExecutor executor;

    public PlayerUpdater(ConfigurationSection section) {
        if (section == null) section = new YamlConfiguration();
        String queueClass = section.getString("queue", "java.util.concurrent.LinkedBlockingQueue");
        BlockingQueue<Runnable> queue = null;
        try {
            queue = (BlockingQueue<Runnable>) Class.forName(queueClass).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            DebugLogger.debug(e);
            I18N.warning("thread.error.create-thread-pool-failed");
        }
        executor = new ThreadPoolExecutor(
                section.getInt("core-pool-size", 1),
                section.getInt("max-pool-size", 1),
                section.getLong("keep-alive-time", 0),
                TimeUnit.MICROSECONDS,
                queue == null ? new LinkedBlockingDeque<>() : queue
        );
    }

    public void submit(Runnable runnable) {
        executor.execute(runnable);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
