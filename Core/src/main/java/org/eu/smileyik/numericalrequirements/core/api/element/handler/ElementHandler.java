package org.eu.smileyik.numericalrequirements.core.api.element.handler;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber.DoubleElementBar;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface ElementHandler {
    default void handlePlayer(NumericalPlayer player, PlayerValue value) {

    }

    default void handlePlayer(NumericalPlayer player, DoubleElementBar value) {

    }

    static ElementHandler getInstance(String className, ConfigurationSection section) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> aClass = Class.forName(className);
        if (!ElementHandler.class.isAssignableFrom(aClass)) {
            return null;
        }
        Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(ConfigurationSection.class);
        return (ElementHandler) declaredConstructor.newInstance(section);
    }
}
