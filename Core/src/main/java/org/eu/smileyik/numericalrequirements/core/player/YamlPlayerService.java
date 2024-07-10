package org.eu.smileyik.numericalrequirements.core.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;

import java.io.File;
import java.io.IOException;

public class YamlPlayerService extends AbstractPlayerService implements Listener {

    public YamlPlayerService(NumericalRequirements plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private File getPlayerDataFile(String playerName) {
        File dataFolder = getPlugin().getDataFolder();
        File playerDataFolder = new File(dataFolder, "PlayerData");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
        return new File(playerDataFolder, String.format("%s.yml", playerName));
    }


    @Override
    protected ConfigurationSection loadPlayerData(Player player) {
        File dataFile = getPlayerDataFile(player.getName());
        return YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    protected void storePlayerData(ConfigurationSection section, Player player) {
        File playerDataFile = getPlayerDataFile(player.getName());
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("name", player.getName());
        configuration.set(CONFIG_PATH_DATA, section);
        try {
            configuration.save(playerDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            joinPlayer(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            quitPlayer(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            quitPlayer(event.getPlayer());
        });
    }
}
