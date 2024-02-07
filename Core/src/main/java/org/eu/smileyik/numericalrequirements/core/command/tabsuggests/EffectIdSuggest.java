package org.eu.smileyik.numericalrequirements.core.command.tabsuggests;

import org.eu.smileyik.numericalrequirements.core.RegisterInfo;
import org.eu.smileyik.numericalrequirements.core.command.TabSuggest;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;

import java.util.List;
import java.util.stream.Collectors;

public class EffectIdSuggest implements TabSuggest {
    private final EffectService service;


    public EffectIdSuggest(EffectService service) {
        this.service = service;
    }

    @Override
    public String getKeyword() {
        return "effect";
    }

    @Override
    public List<String> suggest() {
        return service.getRegisteredEffects()
                .stream()
                .map(RegisterInfo::getId)
                .collect(Collectors.toList());
    }
}
