package org.eu.smileyik.numericalrequirements.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TestCommandClient {
    public static void sendPacket(DatagramSocket socket, String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("127.0.0.1"), 60555);
        socket.send(packet);
    }

    public static void luaConsole(DatagramSocket socket, Scanner scanner) throws IOException {
        System.out.println("input 'exit' to quit");
        sendPacket(socket, "lua run init console");
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();
            if (command.equals("exit")) {
                break;
            }
            sendPacket(socket, "lua run " + command);
        }
        sendPacket(socket, "lua run close console");
    }

    public static void main(String[] args) throws SocketException {
        try (DatagramSocket socket = new DatagramSocket()) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String s = scanner.nextLine();
                if (s.equals("lua")) {
                    luaConsole(socket, scanner);
                    continue;
                }
                sendPacket(socket, s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
