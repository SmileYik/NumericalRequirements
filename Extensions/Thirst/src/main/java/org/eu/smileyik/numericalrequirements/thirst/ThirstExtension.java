package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectService;
import org.eu.smileyik.numericalrequirements.core.api.effect.impl.ElementBoundedEffect;
import org.eu.smileyik.numericalrequirements.core.api.effect.impl.ElementNaturalDepletionEffect;
import org.eu.smileyik.numericalrequirements.core.api.effect.impl.ElementRateEffect;
import org.eu.smileyik.numericalrequirements.core.api.element.handler.ElementHandler;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.thirst.listener.DeathPunishment;
import org.eu.smileyik.numericalrequirements.thirst.listener.GetLiquid;

import java.io.File;

public class ThirstExtension extends Extension {
    private YamlConfiguration config;

    private ThirstElement thirstElement;
    private ThirstLoreTag thirstLoreTag;
    private ThirstNBTTag thirstNBTTag;
    private ElementNaturalDepletionEffect elementNaturalDepletionEffect;
    private ElementRateEffect elementRateEffect;
    private ElementBoundedEffect elementBoundedEffect;

    private ElementHandler elementHandler;

    private DeathPunishment deathPunishment;

    @Override
    public void onEnable() {
        saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        NumericalRequirements api = NumericalRequirements.getInstance();
        if (!config.getBoolean("enable", true)) {
            api.getExtensionService().unregister(this);
            return;
        }

        try {
            elementHandler = ElementHandler.getInstance(
                    config.getString("thirst.effect-handler"),
                    config.getConfigurationSection("thirst.effects")
            );
        } catch (Exception e) {
            I18N.warning("extensions.thirst.load-effect-handler-failed", e);
            elementHandler = new ElementHandler() {};
        }
        thirstElement = new ThirstElement(this, config, elementHandler);
        thirstLoreTag = new ThirstLoreTag(thirstElement, config, elementHandler);
        thirstNBTTag = new ThirstNBTTag(thirstElement, elementHandler);

        api.getElementService().registerElement(thirstElement);
        api.getItemService().registerItemTag(thirstLoreTag);
        api.getItemService().registerItemTag(thirstNBTTag);
        EffectService effectService = api.getEffectService();
        effectService.addBundleConfigSection(config.getConfigurationSection("thirst.effect-bundles"));

        elementBoundedEffect = new ElementBoundedEffect(thirstElement);
        effectService.registerEffect(elementBoundedEffect);

        elementNaturalDepletionEffect = new ElementNaturalDepletionEffect(thirstElement);
        effectService.registerEffect(elementNaturalDepletionEffect);

        elementRateEffect = new ElementRateEffect(thirstElement);
        effectService.registerEffect(elementRateEffect);

        if (config.getBoolean("thirst.death-punishment.enable")) {
            ConfigurationSection section = config.getConfigurationSection("thirst.death-punishment");
            deathPunishment = new DeathPunishment(section, thirstElement);
            NumericalRequirements.getPlugin().getServer().getPluginManager()
                    .registerEvents(deathPunishment, NumericalRequirements.getPlugin());
        }

        ConfigurationSection getLiquidConfig = config.getConfigurationSection("get-liquid");
        if (getLiquidConfig == null) getLiquidConfig = new YamlConfiguration();
        for (String key : getLiquidConfig.getKeys(false)) {
            ConfigurationSection section = getLiquidConfig.getConfigurationSection(key);
            if (section.getBoolean("enable", true)) {
                NumericalRequirements.getPlugin().getServer().getPluginManager()
                        .registerEvents(new GetLiquid(section), NumericalRequirements.getPlugin());
            }
        }
    }

    @Override
    public void onDisable() {
        NumericalRequirements api = NumericalRequirements.getInstance();
        api.getItemService().unregisterItemTag(thirstLoreTag);
        api.getElementService().unregisterElement(thirstElement);
        api.getEffectService().unregisterEffect(elementBoundedEffect);
        api.getEffectService().unregisterEffect(elementRateEffect);
        api.getEffectService().unregisterEffect(elementNaturalDepletionEffect);

        config = null;
        thirstLoreTag = null;
        thirstElement = null;
        elementHandler = null;
        deathPunishment = null;
    }
}
