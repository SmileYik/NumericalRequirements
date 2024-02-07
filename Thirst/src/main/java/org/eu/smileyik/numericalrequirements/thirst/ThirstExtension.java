package org.eu.smileyik.numericalrequirements.thirst;

import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementBoundedEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementNaturalDepletionEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ElementRateEffect;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;
import org.eu.smileyik.numericalrequirements.core.extension.Extension;

public class ThirstExtension extends Extension {
    public static ThirstElement element = new ThirstElement();
    @Override
    protected void onEnable() {
        NumericalRequirements api = NumericalRequirements.getInstance();
        api.getElementService().registerElement(element);
        api.getItemService().registerItemTag(new ThirstTag());
        EffectService effectService = api.getEffectService();
        effectService.registerEffect(new ElementNaturalDepletionEffect(element));
        effectService.registerEffect(new ElementBoundedEffect(element));
        effectService.registerEffect(new ElementRateEffect(element));
        System.out.println("Be enabled!");
    }

    @Override
    protected void onDisable() {

    }
}
