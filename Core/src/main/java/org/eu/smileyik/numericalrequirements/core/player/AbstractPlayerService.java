package org.eu.smileyik.numericalrequirements.core.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.api.Updatable;
import org.eu.smileyik.numericalrequirements.core.api.event.player.NumericalPlayerLoadEvent;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerService;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.concurrent.*;

public abstract class AbstractPlayerService implements PlayerService, Updatable {
    protected static final String CONFIG_PATH_DATA = "data";

    private final NumericalRequirements plugin;
    private final ConcurrentHashMap<Player, NumericalPlayer> players = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture = null;
    private boolean firstSchedule = false;
    private final long scheduleDelay;
    private final long schedulePeriod;
    private final PlayerUpdater playerUpdater;

    private final Updatable autoSave;

    protected AbstractPlayerService(NumericalRequirements plugin) {
        this.plugin = plugin;
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("player");
        scheduleDelay = section.getLong("schedule.delay", 40);
        schedulePeriod = section.getLong("schedule.period", 40);
        playerUpdater = new PlayerUpdater(section.getConfigurationSection("thread-pool.update"));
        autoSave = new AbstractUpdatable() {
            final long period = section.getLong("autosave", 600000);
            @Override
            protected boolean doUpdate(double second) {
                DebugLogger.debug("autosave player data");
                savePlayerData();
                return false;
            }

            @Override
            public long period() {
                return period;
            }
        };
    }

    @Override
    public void loadOnlinePlayers() {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            joinPlayer(onlinePlayer);
        }
    }

    private void start() {
        if (scheduledFuture == null) {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(
                    this, firstSchedule ? scheduleDelay : 0, schedulePeriod, TimeUnit.MILLISECONDS
            );
            firstSchedule = false;
        }
    }

    private void stop() {
        if (players.isEmpty() && scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    @Override
    public synchronized NumericalPlayer getNumericalPlayer(Player player) {
        return players.get(player);
    }

    @Override
    public synchronized boolean isPlayerJoin(Player player) {
        return players.containsKey(player);
    }

    @Override
    public synchronized NumericalPlayer joinPlayer(Player p) {
        NumericalPlayer numericalPlayer = new NumericalPlayerImpl(playerUpdater, p);
        ConfigurationSection configurationSection = loadPlayerData(p);
        ConfigurationSection dataConfig = null;
        if (configurationSection.isConfigurationSection(CONFIG_PATH_DATA)) {
            dataConfig = configurationSection.getConfigurationSection(CONFIG_PATH_DATA);
        } else {
            dataConfig = configurationSection.createSection(CONFIG_PATH_DATA);
        }

        NumericalPlayerLoadEvent event = new NumericalPlayerLoadEvent(true, numericalPlayer, dataConfig);
        plugin.getServer().getPluginManager().callEvent(event);

        players.put(p, numericalPlayer);
        start();
        return numericalPlayer;
    }

    @Override
    public synchronized NumericalPlayer quitPlayer(Player p) {
        NumericalPlayer player = players.remove(p);
        if (player != null) {
            YamlConfiguration configuration = new YamlConfiguration();
            player.store(configuration);
            storePlayerData(configuration, p);
        }
        stop();
        return player;
    }

    @Override
    public synchronized void savePlayerData() {
        players.forEach((k, v) -> {
            YamlConfiguration configuration = new YamlConfiguration();
            v.store(configuration);
            storePlayerData(configuration, k);
        });
    }

    @Override
    public void removeDisabledKey() {
        savePlayerData();
        {
            players.forEach((k, v) -> {
                v.removeDisabledKey();
            });
        }
    }

    @Override
    public void shutdown() {
        playerUpdater.shutdown();
        savePlayerData();
        {
            players.clear();
            stop();
            scheduledExecutorService.shutdown();
        }
    }

    @Override
    public boolean update() {
        autoSave.update();
        players.forEachValue(players.mappingCount(), player -> {
            if (org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements.isAvailableWorld(player.getPlayer())) {
                playerUpdater.submit(player);
            }
        });
        return true;
    }

    @Override
    public long period() {
        return 0;
    }

    public NumericalRequirements getPlugin() {
        return plugin;
    }

    protected abstract ConfigurationSection loadPlayerData(Player player);

    protected abstract void storePlayerData(ConfigurationSection section, Player player);
}
