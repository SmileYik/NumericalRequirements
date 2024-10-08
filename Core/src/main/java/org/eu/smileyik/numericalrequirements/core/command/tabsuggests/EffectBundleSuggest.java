package org.eu.smileyik.numericalrequirements.core.command.tabsuggests;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.command.DefaultTabSuggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectBundleSuggest implements DefaultTabSuggest {

    @Override
    public boolean matches(String[] args, int commandIdx) {
        return "add".equalsIgnoreCase(args[commandIdx]) &&
                commandIdx + 2 < args.length &&
                "EffectBundle".equalsIgnoreCase(args[commandIdx + 2]);
    }

    @Override
    public List<String> suggest(String[] args, int commandIdx) {
        if (args.length == commandIdx + 4) {
            return new ArrayList<>(NumericalRequirements.getInstance().getEffectService().getEffectBundleIds());
        } else if (args.length == commandIdx + 5) {
            return Collections.singletonList(I18N.tr("command.tab-suggest.seconds"));
        }
        return suggest();
    }
}
