package org.eu.smileyik.numericalrequirements.reflect.test;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MySimpleReflectTest {

    static {
        try {
            new DebugLogger(Logger.getLogger("DEBUG"), new File("./build/debug.log"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPath1() {
        return new ReflectClassPathBuilder()
                .newGroup("")
                .newGroup("org.eu.smileyik.numericalrequirements.reflect.test.Entity")
                .newGroup("@")
                .append("pi")
                .endGroup()
                .newGroup("#")
                .append("say(java.lang.String)")
                .endGroup()
                .newGroup("")
                .append("<c1>()")
                .append("<c2>(java.lang.String)")
                .endGroup()
                .append("$ClassA#hello(java.lang.String)")
                .append("$ClassA@a")
                .endGroup()
                .endGroup()
                .finish();
    }

    @Test
    public void pathBuildTest() {
        System.out.println(getPath1());
    }

    @Test
    public void getReflectClassTest() throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        ReflectClass reflectClass = MySimpleReflect.getReflectClass(getPath1(), true);
        Object o1 = reflectClass.newInstance("c1");
        Object o2 = reflectClass.newInstance("c2", "O2!");
        System.out.println(reflectClass.get("pi", o2));
        System.out.println(reflectClass.execute("say", o1, "O1!"));
        Entity.ClassA classA = new Entity.ClassA();
        System.out.println(reflectClass.get("a", classA));
        reflectClass.execute("hello", classA, "classA!");
    }
}
