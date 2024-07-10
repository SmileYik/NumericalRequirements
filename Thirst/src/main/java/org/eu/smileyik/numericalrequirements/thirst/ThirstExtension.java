package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectService;
import org.eu.smileyik.numericalrequirements.core.api.element.handler.ElementHandler;
import org.eu.smileyik.numericalrequirements.core.api.element.handler.RangeHandler;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementBoundedEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementNaturalDepletionEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementRateEffect;
import org.eu.smileyik.numericalrequirements.core.extension.Extension;
import org.eu.smileyik.numericalrequirements.thirst.listener.DeathPunishment;
import org.eu.smileyik.numericalrequirements.thirst.listener.GetLiquid;

import java.io.File;

public class ThirstExtension extends Extension {
    private YamlConfiguration config;

    private ThirstElement thirstElement;
    private ThirstTag thirstTag;
    private ElementNaturalDepletionEffect elementNaturalDepletionEffect;
    private ElementRateEffect elementRateEffect;
    private ElementBoundedEffect elementBoundedEffect;

    private ElementHandler elementHandler;

    private DeathPunishment deathPunishment;

    @Override
    protected void onEnable() {
        saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        NumericalRequirements api = NumericalRequirements.getInstance();
        if (!config.getBoolean("enable", true)) {
            api.getExtensionService().unregister(this);
            return;
        }

        elementHandler = new RangeHandler(config.getConfigurationSection("thirst.effects"));
        thirstElement = new ThirstElement(this, config, elementHandler);
        thirstTag = new ThirstTag(thirstElement, config, elementHandler);

        api.getElementService().registerElement(thirstElement);
        api.getItemService().registerItemTag(thirstTag);
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
    protected void onDisable() {
        NumericalRequirements api = NumericalRequirements.getInstance();
        api.getItemService().unregisterItemTag(thirstTag);
        api.getElementService().unregisterElement(thirstElement);
        api.getEffectService().unregisterEffect(elementBoundedEffect);
        api.getEffectService().unregisterEffect(elementRateEffect);
        api.getEffectService().unregisterEffect(elementNaturalDepletionEffect);

        config = null;
        thirstTag = null;
        thirstElement = null;
        elementHandler = null;
        deathPunishment = null;
    }
}
