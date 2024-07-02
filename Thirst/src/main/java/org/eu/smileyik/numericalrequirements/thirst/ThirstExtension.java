package org.eu.smileyik.numericalrequirements.thirst;

import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementBoundedEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementNaturalDepletionEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementRateEffect;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;
import org.eu.smileyik.numericalrequirements.core.element.handler.ElementHandler;
import org.eu.smileyik.numericalrequirements.core.element.handler.RangeHandler;
import org.eu.smileyik.numericalrequirements.core.extension.Extension;

import java.io.File;

public class ThirstExtension extends Extension {
    private YamlConfiguration config;

    private ThirstElement thirstElement;
    private ThirstTag thirstTag;
    private ElementNaturalDepletionEffect elementNaturalDepletionEffect;
    private ElementRateEffect elementRateEffect;
    private ElementBoundedEffect elementBoundedEffect;

    private ElementHandler elementHandler;

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
    }
}
