package org.eu.smileyik.numericalrequirements.test.nms.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.nms.CraftPlayer;
import org.eu.smileyik.numericalrequirements.nms.entity.EntityPlayer;
import org.eu.smileyik.numericalrequirements.nms.network.NetworkManager;
import org.eu.smileyik.numericalrequirements.nms.network.PlayerConnection;
import org.eu.smileyik.numericalrequirements.test.NeedTest;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;

public class NetworkTest1 implements Listener {
    @NeedTest
    public void init() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {

        NumericalRequirements.getPlugin().getServer().getPluginManager().registerEvents(this, NumericalRequirements.getPlugin());
//        NumericalRequirements.getPlugin().getServer().getScheduler().runTask(
//                NumericalRequirements.getPlugin(), () -> {try {
//                    Class<?> WORLD_SERVER = Class.forName("net.minecraft.server.level.WorldServer");
//                    Field PLAYER_LIST = WORLD_SERVER.getDeclaredField("B");
//                    PLAYER_LIST.setAccessible(true);
//                    World world = NumericalRequirements.getPlugin().getServer().getWorld("World");
//                    Field WORLD = world.getClass().getDeclaredField("world");
//                    WORLD.setAccessible(true);
//                    Object o = WORLD.get(world);
//                    synchronized (o) {
//                        List<?> list = (List<?>) PLAYER_LIST.get(o);
//                        list = new MyList<>(list, o);
//                        getUnsafe().putObject(o, getUnsafe().objectFieldOffset(PLAYER_LIST), list);
//                    }
//                } catch (Throwable t) {
//                    throw new RuntimeException(t);
//                }}
//        );
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        EntityPlayer handle = CraftPlayer.getHandle(player);
        PlayerConnection playerConnection = handle.playerConnection();
        NetworkManager networkManager = playerConnection.getNetworkManager();
        Channel channel = networkManager.channel();
        channel.pipeline().addBefore("packet_handler", "nreq_packet_handle", new MyHandler());
        System.out.println(channel.isOpen());
        System.out.println(channel);

    }

    public static boolean enableLog = false;
    public static boolean enteredBed = false;
    @EventHandler
    public void onPlayerSleep(PlayerInteractEvent event) throws Exception {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().name().contains("BED")) {
            // enteredBed = true;
            Class<?> POSITION = Class.forName("net.minecraft.core.BlockPosition");
            Constructor<?> declaredConstructor = POSITION.getDeclaredConstructor(int.class, int.class, int.class);
            Object o = declaredConstructor.newInstance(
                    event.getClickedBlock().getX(),
                    event.getClickedBlock().getY(),
                    event.getClickedBlock().getZ()
            );
            Class<?> PLAYER = Class.forName("net.minecraft.server.level.EntityPlayer");
            Method declaredMethod = PLAYER.getDeclaredMethod("sleep", POSITION, boolean.class);
            event.setCancelled(true);
            enteredBed = true;
            System.out.println(declaredMethod.invoke(CraftPlayer.getHandle(event.getPlayer()).getInstance(), o, true));
        }
    }

    @EventHandler
    public void onPlayerWakeUp(PlayerBedLeaveEvent event) {
        enteredBed = false;
    }

    public static class MyByteBuddyHandler {
        @RuntimeType
        public static Object intercept(@Super Object superInstance, @This Object thisInstance, @Origin Method method, @SuperMethod Method superMethod, @AllArguments Object ... args) throws InvocationTargetException, IllegalAccessException {
//            System.out.println(superInstance.getClass());
//            System.out.println(thisInstance.getClass());
//            System.out.println(method.getName());
           //  System.out.printf("proxy method %s(%s)\n", method.getName(), args == null ? "" : Arrays.stream(args).map(it -> it == null ? "null" : it.getClass().getSimpleName()).collect(Collectors.joining(", ")));
            Object invoke = superMethod.invoke(thisInstance, args);
            // System.out.printf("returned %s type value: %s\n", method.getReturnType().getSimpleName(), invoke);
            return invoke;
        }
    }

    public static class MyList <T> extends ArrayList<T> {
        private final Object world;
        private final Class<?> ENTITY_PLAYER_CLASS = Class.forName("net.minecraft.server.level.EntityPlayer");
        private final Class<?> PROXIED_PLAYER_CLASS;
        private Map<T, T> proxiedMay = new HashMap<>();

        public MyList(List<T> list, Object world) throws ClassNotFoundException {
            super(list);
            this.world = world;
            PROXIED_PLAYER_CLASS = new ByteBuddy()
                    .subclass(ENTITY_PLAYER_CLASS)
                    .name("net.minecraft.server.level.EntityPlayer$MyEntityPlayer")
                    .method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(MyByteBuddyHandler.class))
                    .make()
                    .load(MyList.class.getClassLoader())
                    .getLoaded();
        }

        private T getProxiedInstance(T obj) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            if (proxiedMay.containsKey(obj)) return (T) proxiedMay.get(obj);

            synchronized (world) {
                Constructor<?> declaredConstructor = PROXIED_PLAYER_CLASS.getDeclaredConstructors()[0];
                Object[] objs = new Object[declaredConstructor.getParameterTypes().length];
                int i = 0;
                for (Class<?> parameterType : declaredConstructor.getParameterTypes()) {
                    objs[i++] = getField(parameterType, obj);
                    System.out.println(parameterType + ": " + objs[i-1]);
                }
                objs[1] = world;
                T proxied = (T) declaredConstructor.newInstance(objs);

                for (Field field : getFields(obj.getClass())) {
                    if (Modifier.isFinal(field.getModifiers())) continue;
                    field.setAccessible(true);
                    Object o = field.get(obj);
                    field.set(proxied, o);
                }

                proxiedMay.put(obj, proxied);
            }


            return proxiedMay.getOrDefault(obj, obj);
        }

        private List<Field> getFields(Class<?> clazz) {
            List<Field> fields = new ArrayList<>();
            while (clazz != null) {
                fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass();
            }
            return fields;
        }

        private Object getField(Class<?> type, Object instance) throws IllegalAccessException {
            Class<?> aClass = instance.getClass();
            while (aClass != null) {
                for (Field field : aClass.getDeclaredFields()) {
                    if (field.getType() == type) {
                        field.setAccessible(true);
                        return field.get(instance);
                    }
                }
                aClass = aClass.getSuperclass();
            }
            return null;
        }

        @Override
        public T get(int index) {
            T t = super.get(index);
            try {
                return getProxiedInstance(t);
            } catch (Throwable e) {
                e.printStackTrace();
                return t;
            }
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            super.forEach(t -> {
                try {
                    action.accept(getProxiedInstance(t));
                } catch (Throwable e) {
                    action.accept(t);
                    e.printStackTrace();
                }
            });
        }

        @Override
        public Iterator<T> iterator() {
            return new MyItr<>(this, super.iterator());
        }

        static class MyItr<T> implements Iterator<T> {
            final MyList<T> myList;
            final Iterator<T> parent;

            MyItr(MyList<T> myList, Iterator<T> parent) {
                this.myList = myList;
                this.parent = parent;
            }

            @Override
            public boolean hasNext() {
                return parent.hasNext();
            }

            @Override
            public T next() {
                T next = parent.next();
                try {
                    return myList.getProxiedInstance(next);
                } catch (Throwable e) {
                    e.printStackTrace();
                    return next;
                }
            }
        }
    }

    public static Map<Class<?>, List<Field>> fieldCache = new HashMap<>();
    public static synchronized String print(Class<?> aClass, Object obj, Set<Object> checked) throws IllegalAccessException {
        if (aClass.getSimpleName().contains("BlockStateList")) return "[blocks]";
        if (checked.contains(obj)) return "";
        if (aClass.isEnum() || Enum.class.isAssignableFrom(aClass)) return obj.toString();
        // if (aClass.isArray()) return Arrays.toString(Array.obj);
        if (!fieldCache.containsKey(aClass)) {
            Class<?> next = aClass;
            List<Field> fields = new ArrayList<>();
            while (next != null) {
                for (Field field : next.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    field.setAccessible(true);
                    fields.add(field);
                }
                next = next.getSuperclass();
            }
            fieldCache.put(aClass, fields);
        }
        List<Field> fields = fieldCache.get(aClass);

        List<String> strings = new ArrayList<>();
        for (Field field : fields) {
            Object o = field.get(obj);
            String oStr = o == null ? "null" : (o.getClass().getName().contains("minecraft") ? print(o.getClass(), o, checked) : String.valueOf(o));
            strings.add(String.format("%s: %s", field.getName(), oStr));
            checked.add(obj);
        }
        return String.format("%s{ %s }", aClass.getSimpleName(), String.join(", ", strings));
    }

    public static Set<String> loggable = new HashSet<>();
    public static class MyHandler extends ChannelDuplexHandler {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg != null) {
                Class<?> aClass = msg.getClass();
                boolean flag = true;
                if (loggable.contains(aClass.getSimpleName())) System.out.println(print(aClass, msg, new HashSet<>()));
                if (!flag) {
                    System.out.println("disabled");
                    return;
                }
                super.channelRead(ctx, msg);
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            boolean flag = true;


            if (msg != null) {
                Class<?> aClass = msg.getClass();

                if (loggable.contains(aClass.getSimpleName())) System.out.println(print(aClass, msg, new HashSet<>()));
                if (!flag) {
                    System.out.println("disabled");
                    return;
                }

                super.write(ctx, msg, promise);
            }
        }
    }
}
