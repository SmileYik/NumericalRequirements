package org.eu.smileyik.numericalrequirements.core.command;

import java.util.Collections;
import java.util.List;

public interface TabSuggest {

    String getKeyword();

    default List<String> suggest() {
        return Collections.emptyList();
    }

    default List<String> suggest(String[] args, int commandIdx) {
        return suggest();
    }
}
