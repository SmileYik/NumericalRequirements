package org.eu.smileyik.numericalrequirements.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestCommandServer {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ExecutorService thread = Executors.newSingleThreadExecutor();

    private final Map<String, Map<String, Method>> commands = new HashMap<>();
    private final Map<String, Object> instances = new HashMap<>();

    private TestCommandServer(int port) {
        thread.execute(() -> {
            lock.writeLock().lock();
        });
        new Thread(() ->{
            try (DatagramSocket socket = new DatagramSocket(port)) {
                socket.setSoTimeout(5);
                System.out.println("Listening on port " + port);
                while (true) {
                    if (lock.writeLock().tryLock()) {
                        try {
                            System.out.println("Test command server stopped");
                            break;
                        } finally {
                            lock.writeLock().unlock();
                        }
                    }
                    byte[] bytes = new byte[8192];
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    try {
                        socket.receive(packet);
                        String str = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Recived command: " + str);
                        try {
                            dispatch(str);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (SocketTimeoutException ignore) {

                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static TestCommandServer start(int port) {
        return new TestCommandServer(port);
    }

    public void stop() {
        thread.execute(() -> {
            lock.writeLock().unlock();
        });
    }

    private void dispatch(String command) throws InvocationTargetException, IllegalAccessException {
        String[] split = command.split(" ");
        if (split.length <= 2) {
            System.out.println("Unknown command: " + command);
            return;
        }
        if (split[0].isEmpty() || split[1].isEmpty()) {
            System.out.println("Unknown command: " + command);
        }

        commands.get(split[0]).get(split[1]).invoke(instances.get(split[0]), (Object) Arrays.copyOfRange(split, 2, split.length));
    }

    public void registerCommandClass(Class<?> clazz, String command) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object o = clazz.getDeclaredConstructor().newInstance();
        instances.put(command, o);
        commands.putIfAbsent(command, new HashMap<>());
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            System.out.println("Registered Command: " + command + " " + declaredMethod.getName());
            commands.get(command).put(declaredMethod.getName(), declaredMethod);
        }
    }

    public static void main(String[] args) throws IOException {
        start(60555);
    }

}
