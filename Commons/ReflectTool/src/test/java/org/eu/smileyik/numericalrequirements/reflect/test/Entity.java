package org.eu.smileyik.numericalrequirements.reflect.test;

import java.util.Arrays;
import java.util.Objects;

public class Entity {

    private double pi = 3.14159265358979323846;

    public Entity() {
        System.out.println("Entity created");
    }

    public Entity(String name) {
        System.out.println("Entity created: " + name);
    }

    public void say(String msg) {
        System.out.println("say " + msg);
    }

    public void hello(String name) {
        System.out.println("Hello " + name);
    }

    public void hello(String[] name) {
        System.out.println("Hello " + Arrays.toString(name));
    }

    public int total(Integer[] array) {
        return Arrays.stream(array).filter(Objects::nonNull).mapToInt(it -> it).sum();
    }

    public static class ClassA {
        public String a = "I am a";
        public void hello(String name) {
            System.out.println("A Hello " + name);
        }
        public void say(String msg) {
            System.out.println("A say " + msg);
        }
    }
}
