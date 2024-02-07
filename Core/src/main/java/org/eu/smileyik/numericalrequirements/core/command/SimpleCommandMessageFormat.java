package org.eu.smileyik.numericalrequirements.core.command;

import org.eu.smileyik.numericalrequirements.core.I18N;

public class SimpleCommandMessageFormat implements CommandMessageFormat, CommandTranslator {
    @Override
    public String notFound() {
        return I18N.tr("command", "command.error.not-found");
    }

    @Override
    public String notFound(String suggestCommandHelp) {
        return I18N.tr("command", "command.error.not-found-with-suggest", suggestCommandHelp);
    }

    @Override
    public String commandError() {
        return I18N.tr("command", "command.error.command-error");
    }

    @Override
    public String commandError(String suggestCommandHelp) {
        return I18N.tr("command", "command.error.command-error-with-suggest", suggestCommandHelp);
    }

    @Override
    public String notPlayer() {
        return I18N.tr("command", "command.error.not-player");
    }

    @Override
    public String notPermission() {
        return I18N.tr("command", "command.error.no-permission");
    }

    @Override
    public String tr(String key, Object ... objs) {
        return I18N.tr(key, objs);
    }
}
