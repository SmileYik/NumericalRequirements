package org.eu.smileyik.numericalrequirements.core.command;

import java.util.List;

public interface DefaultTabSuggest extends TabSuggest {
    boolean matches(String[] args, int commandIdx);

    @Override
    default String getKeyword() {
        return "";
    }
}
