package org.eu.smileyik.numericalrequirements.core.extension.placeholderapi;

import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.core.api.extension.placeholder.PlaceholderRequestCallback;
import org.eu.smileyik.numericalrequirements.core.extension.ExtensionDescription;

public class PlaceholderApiExtension extends Extension {
    private PlaceholderExpansion placeholder;

    public PlaceholderApiExtension() {
        super(new ExtensionDescription(
                "placeholderapi_extension",
                "PlaceholderApiExtension",
                "SmileYik",
                "1.0.0",
                "Add placeholder"
        ));

    }

    public void addPlaceholder(PlaceholderRequestCallback callback) {
        if (placeholder != null) {
            placeholder.addCallback(callback);
            return;
        }

        if(getPlugin().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholder = new PlaceholderExpansion(getApi().getPlayerService());
            getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                placeholder.register();
                placeholder.addCallback(callback);
            });
        }
    }

    public void removePlaceholder(PlaceholderRequestCallback callback) {
        if (placeholder != null) {
            placeholder.removeCallback(callback);
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if (placeholder != null) {
            placeholder.clear();
        }
    }
}
