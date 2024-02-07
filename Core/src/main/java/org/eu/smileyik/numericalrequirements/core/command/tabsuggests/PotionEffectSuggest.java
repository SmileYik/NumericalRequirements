package org.eu.smileyik.numericalrequirements.core.command.tabsuggests;

import org.bukkit.potion.PotionEffectType;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.command.DefaultTabSuggest;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PotionEffectSuggest implements DefaultTabSuggest {
    @Override
    public boolean matches(String[] args, int commandIdx) {
        return "add".equalsIgnoreCase(args[commandIdx]) &&
                commandIdx + 2 < args.length &&
                "PotionEffect".equalsIgnoreCase(args[commandIdx + 2]);
    }

    @Override
    public List<String> suggest(String[] args, int commandIdx) {
        if (args.length == commandIdx + 4) {
            return Arrays.stream(PotionEffectType.values())
                        .map(PotionEffectType::getName)
                        .collect(Collectors.toList());
        } else if (args.length == commandIdx + 5) {
            return Collections.singletonList(I18N.tr("command.tab-suggest.seconds"));
        } else if (args.length == commandIdx + 6) {
            return Collections.singletonList(I18N.tr("command.tab-suggest.amplifier"));
        }
        return suggest();
    }
}
