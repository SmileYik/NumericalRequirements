package org.eu.smileyik.numericalrequirements.reflect.test;

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

    public static class ClassA {
        public String a = "I am a";
        public void hello(String name) {
            System.out.println("Hello " + name);
        }
    }
}
