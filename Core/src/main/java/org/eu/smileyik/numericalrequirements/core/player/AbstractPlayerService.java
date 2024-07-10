package org.eu.smileyik.numericalrequirements.core.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.event.player.NumericalPlayerLoadEvent;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerService;

import java.util.concurrent.*;

public abstract class AbstractPlayerService implements PlayerService {
    private final NumericalRequirements plugin;
    private final ConcurrentHashMap<Player, NumericalPlayer> players = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    // private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    protected final String CONFIG_PATH_DATA = "data";
    private ScheduledFuture<?> scheduledFuture = null;

    protected AbstractPlayerService(NumericalRequirements plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadOnlinePlayers() {
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            joinPlayer(onlinePlayer);
        }
    }

    private void start() {
        if (scheduledFuture == null) {
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::update, 40, 40, TimeUnit.MILLISECONDS);
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
        NumericalPlayer numericalPlayer = new NumericalPlayerImpl(p);
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
        savePlayerData();
        {
            players.clear();
            stop();
            // executorService.shutdownNow();
            scheduledExecutorService.shutdown();
        }
    }

    @Override
    public boolean update() {
        players.forEachValue(players.mappingCount(), player -> {
            if (org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements.isAvailableWorld(player.getPlayer())) {
                player.update();
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
