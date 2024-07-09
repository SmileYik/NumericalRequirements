package org.eu.smileyik.numericalrequirements.test.commands;

import org.eu.smileyik.numericalrequirements.nms.ReflectPathGenerator;
import org.eu.smileyik.numericalrequirements.test.TestCommand;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@TestCommand("reflect")
public class ReflectCommand {

    public void dump(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {
        ReflectPathGenerator.main(args);
    }
}
