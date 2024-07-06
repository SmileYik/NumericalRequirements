package org.eu.smileyik.numericalrequirements.reflect.test;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;
import org.eu.smileyik.numericalrequirements.reflect.builder.ReflectClassBuilder;
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

    private ReflectClassBuilder getReflectClassBuilder() {
        return new ReflectClassBuilder("org.eu.smileyik.numericalrequirements.reflect.test.Entity")
                .field("pi")
                .method("say").arg("java.lang.String").finished()
                .method("hello").arg("java.lang.String").finished()
                .method("hello", "helloNames").arg("java.lang.String[]").finished()
                .method("total").arg("java.lang.Integer[]").finished()
                .constructor("c1").finished()
                .constructor("c2").args("java.lang.String")
                .innerClass("ClassA")
                .field("a", "pi2")
                .method("say", "aSay").args("java.lang.String")
                .method("hello", "aHello").args("java.lang.String")
                .toInnerClass()
                .finished();

    }

    private String getPath2() {
        return getReflectClassBuilder().toPrettyString();
    }

    @Test
    public void pathBuildTest() {
        System.out.println(getPath1());
        System.out.println(getPath2());
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

    @Test
    public void getReflectClass2Test() throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        ReflectClass reflectClass = MySimpleReflect.getReflectClass(getPath2(), true);
        Object o1 = reflectClass.newInstance("c1");
        Object o2 = reflectClass.newInstance("c2", "O2!");
        System.out.println(reflectClass.get("pi", o2));
        reflectClass.execute("say", o1, "O1!");
        reflectClass.execute("hello", o1, "O1!");
        Entity.ClassA classA = new Entity.ClassA();
        System.out.println(reflectClass.get("pi2", classA));
        reflectClass.execute("aHello", classA, "classA!");
        reflectClass.execute("aSay", classA, "classA!");
    }

    @Test
    public void getReflectClass3Test() throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        ReflectClass reflectClass = getReflectClassBuilder().toClassForce();
        Object o1 = reflectClass.newInstance("c1");
        Object o2 = reflectClass.newInstance("c2", "O2!");
        System.out.println(reflectClass.get("pi", o2));
        reflectClass.execute("say", o1, "O1!");
        reflectClass.execute("hello", o1, "O1!");
        System.out.println(reflectClass.execute("total", o1, (Object) new Integer[] {1, 2, 3}));
        reflectClass.execute("helloNames", o1, (Object) new String[] { "a", "b", "c" });
        Entity.ClassA classA = new Entity.ClassA();
        System.out.println(reflectClass.get("pi2", classA));
        reflectClass.execute("aHello", classA, "classA!");
        reflectClass.execute("aSay", classA, "classA!");
    }
}
