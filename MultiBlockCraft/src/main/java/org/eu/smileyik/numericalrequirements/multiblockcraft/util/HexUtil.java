package org.eu.smileyik.numericalrequirements.multiblockcraft.util;

public class HexUtil {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static String bytesToHex(Byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static byte[] hexToBytes(String hex) {
        char[] charArray = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 1; i < bytes.length; i += 2) {
            bytes[i >> 1] =
                    (byte) ((Byte.parseByte(String.valueOf(charArray[i - 1]), 16) << 4) |
                                                Byte.parseByte(String.valueOf(charArray[i]), 16));
        }
        return bytes;
    }
}
