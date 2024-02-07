package org.eu.smileyik.numericalrequirements.core.command.exception;

public class NoRootCommandException extends RuntimeException {
    public NoRootCommandException() {
        super("There is no root command in the classes!");
    }
}
