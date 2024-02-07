package org.eu.smileyik.numericalrequirements.core.command;

public interface CommandMessageFormat {
    String notFound();
    String notFound(String suggestCommandHelp);

    String commandError();
    String commandError(String suggestCommandHelp);

    String notPlayer();
    String notPermission();
}
