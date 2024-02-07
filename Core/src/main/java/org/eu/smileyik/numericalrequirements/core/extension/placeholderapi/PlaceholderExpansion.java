package org.eu.smileyik.numericalrequirements.core.extension.placeholderapi;

import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.service.PlayerService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    private final PlayerService playerService;
    private final List<PlaceholderRequestCallback> callbackList = new ArrayList<>();
    public PlaceholderExpansion(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nreq";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SmileYik";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    public void clear() {
        callbackList.clear();
    }

    protected synchronized void addCallback(PlaceholderRequestCallback callback) {
        callbackList.add(callback);
    }
    protected synchronized void removeCallback(PlaceholderRequestCallback callback) {
        callbackList.remove(callback);
    }
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;
        NumericalPlayer numericalPlayer = playerService.getNumericalPlayer(player);
        if (numericalPlayer == null) {
            return null;
        }
        synchronized (this) {
            for (PlaceholderRequestCallback callback : callbackList) {
                String s = callback.onRequest(numericalPlayer, params);
                if (s != null) {
                    return s;
                }
            }
        }
        return null;
    }
}
