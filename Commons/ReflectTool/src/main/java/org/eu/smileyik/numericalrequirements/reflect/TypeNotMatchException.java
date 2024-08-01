package org.eu.smileyik.numericalrequirements.reflect;

import java.util.Arrays;

public class TypeNotMatchException extends RuntimeException {
    public TypeNotMatchException(String currentType, String needType) {
        super(String.format("Type '%s' does not match required type '%s'", currentType, needType));
    }

    public TypeNotMatchException(String[] currentTypes, String[] needTypes) {
        super(String.format("Type '%s' does not match required type '%s'", Arrays.toString(currentTypes), Arrays.toString(needTypes)));
    }
}
