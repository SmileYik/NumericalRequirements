package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.creater;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;

import java.util.HashMap;
import java.util.Map;

public class ChatInteract implements Listener {
    private final Map<String, Step> steps;
    private final Player player;
    private final Map<String, String> map = new HashMap<>();
    private final Callback callback;
    private final String i18nPrefix;
    private String currentStep;

    public ChatInteract(Map<String, Step> steps, Player player, Callback callback, String i18nPrefix) {
        this.steps = steps;
        this.player = player;
        this.callback = callback;
        this.i18nPrefix = i18nPrefix;
    }

    public void start(String step) {
        this.currentStep = step;
        if (this.currentStep != null) {
            Msg.trMsg(player, i18nPrefix + ".step.help");
            Msg.trMsg(player, i18nPrefix + ".step." + currentStep + ".help");
            MultiBlockCraftExtension extension = MultiBlockCraftExtension.getInstance();
            extension.getPlugin().getServer().getPluginManager().registerEvents(this, extension.getPlugin());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() != player) return;
        event.setCancelled(true);
        String message = event.getMessage();
        if ("help".equalsIgnoreCase(message)) {
            Msg.trMsg(player, i18nPrefix + ".step.help");
            Msg.trMsg(player, i18nPrefix + ".step." + currentStep + ".help");
            return;
        } else if ("exit".equalsIgnoreCase(message)) {
            event.getHandlers().unregister(this);
            Msg.trMsg(player, i18nPrefix + ".stop");
            return;
        }
        String nextStep = steps.get(currentStep).apply(map, player, i18nPrefix, message);
        if (nextStep == null) {
            // finished
            event.getHandlers().unregister(this);
            callback.callback(map);
            return;
        }
        if (!nextStep.equalsIgnoreCase(currentStep)) {
            currentStep = nextStep;
            Msg.trMsg(player, i18nPrefix + ".step.help");
            Msg.trMsg(player, i18nPrefix + ".step." + currentStep + ".help");
        }

    }

    public static Step newSimpleStep(String stepName, String nextStep, String key) {
        return ((map, p, prefix, str) -> {
            if (str == null) return stepName;
            map.put(key, str);
            Msg.trMsg(p, prefix + ".step." + stepName + ".display", str);
            return nextStep;
        });
    }

    public interface Step {
        String apply(Map<String, String> map, Player p, String i18nPrefix, String str);
    }

    public interface Callback {
        void callback(Map<String, String> map);
    }
}
