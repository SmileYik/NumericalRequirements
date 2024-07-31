package org.eu.smileyik.numericalrequirements.test.nms;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.event.Listener;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.nms.network.PlayerConnection;
import org.eu.smileyik.numericalrequirements.test.NeedTest;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Collectors;

// @NeedTest
public class ProxyNMSTest implements Listener {
    private Class<?> proxyClass;
    @NeedTest
    public void a() throws ClassNotFoundException, NoSuchMethodException {
        NumericalRequirements.getPlugin().getServer().getPluginManager().registerEvents(this, NumericalRequirements.getPlugin());
        Class<?> clazz = Class.forName("net.minecraft.server.network.PlayerConnection");
        Class<?> packetInClazz = Class.forName("net.minecraft.network.protocol.game.PacketListenerPlayIn");
        proxyClass = new ByteBuddy()
                .subclass(clazz)
                .name("net.minecraft.server.network.PlayerConnection$NREQProxy")
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(MyByteBuddyHandler.class))
                .defineField("instance", PlayerConnection.class, Modifier.PUBLIC)
                .implement(MyByteBuddyHandlerInterface.class)
                .intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(ProxyNMSTest.class.getClassLoader())
                .getLoaded();

//        ByteBuddyAgent.install();
//        proxyClass = new ByteBuddy()
//                .redefine(clazz)
//                .method(ElementMatchers.isPublic())
//                .intercept(MethodDelegation.to(MyByteBuddyHandler2.class))
//                .make()
//                .load(clazz.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
//                .getLoaded();

        System.out.println(Arrays.toString(proxyClass.getDeclaredMethods()));
        System.out.println(Arrays.toString(proxyClass.getMethods()));
        System.out.println(Arrays.toString(proxyClass.getDeclaredFields()));
        System.out.println(Arrays.toString(proxyClass.getFields()));
        System.out.println(Arrays.toString(proxyClass.getSuperclass().getDeclaredFields()));
        System.out.println(Arrays.toString(proxyClass.getSuperclass().getFields()));
    }

    public Unsafe unsafe;
    public Unsafe getUnsafe() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (unsafe == null) {
            Constructor<Unsafe> declaredConstructor = Unsafe.class.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            unsafe = declaredConstructor.newInstance();
        }
        return unsafe;
    }

    private void copyFieldsToSubclass(Object p, Object q) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        // p to q
        copyFields(p, q, p.getClass());
    }

    private void copyFields(Object p, Object q, Class<?> clazz) throws NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            copyField(declaredField, p, q);
        }
        if (clazz.getSuperclass() != null) {
            copyFields(p, q, clazz.getSuperclass());
        }
    }

    private void copyField(Field field, Object p, Object q) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        field.setAccessible(true);
        Object x = field.get(p);

        if (Modifier.isStatic(field.getModifiers())) {
            getUnsafe().putObject(q, getUnsafe().staticFieldOffset(field), x);
        } else {
            getUnsafe().putObject(q, getUnsafe().objectFieldOffset(field), x);
        }
    }

    public interface MyByteBuddyHandlerInterface {
        PlayerConnection getInstance();
        void setInstance(PlayerConnection instance);
    }

    public static class MyByteBuddyHandler {


        @RuntimeType
        public static Object intercept(@Super Object superInstance, @This MyByteBuddyHandlerInterface thisInstance, @Origin Method method, @AllArguments Object ... args) throws InvocationTargetException, IllegalAccessException {
            System.out.println(superInstance.getClass());
            System.out.println(thisInstance.getClass());
            System.out.println(method.getName());
            System.out.printf("proxy method %s(%s)\n", method.getName(), args == null ? "" : Arrays.stream(args).map(it -> it == null ? "null" : it.getClass().getSimpleName()).collect(Collectors.joining(", ")));
            Object invoke = method.invoke(thisInstance.getInstance().getInstance(), args);
            System.out.printf("returned %s type value: %s\n", method.getReturnType().getSimpleName(), invoke);
            return invoke;
        }
    }

    public static class MyByteBuddyHandler2 {


        @RuntimeType
        public static Object intercept(@Super Object superInstance, @This Object thisInstance, @Origin Method method, @AllArguments Object ... args) throws InvocationTargetException, IllegalAccessException {
            System.out.println(superInstance.getClass());
            System.out.println(thisInstance.getClass());
            System.out.println(method.getName());
            System.out.printf("proxy method %s(%s)\n", method.getName(), args == null ? "" : Arrays.stream(args).map(it -> it == null ? "null" : it.getClass().getSimpleName()).collect(Collectors.joining(", ")));
            Object invoke = method.invoke(thisInstance, args);
            System.out.printf("returned %s type value: %s\n", method.getReturnType().getSimpleName(), invoke);
            return invoke;
        }
    }

    public static final class MyHandler implements InvocationHandler {

        private final PlayerConnection playerConnection;

        public MyHandler(PlayerConnection playerConnection) {
            this.playerConnection = playerConnection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.printf("proxy method %s(%s)\n", method.getName(), args == null ? "" : Arrays.stream(args).map(it -> it == null ? "null" : it.getClass().getSimpleName()).collect(Collectors.joining(", ")));
            Object invoke = method.invoke(playerConnection.getInstance(), args);
            System.out.printf("returned %s type value: %s\n", method.getReturnType().getSimpleName(), invoke);
            return invoke;
        }
    }
}
