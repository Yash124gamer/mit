package utils;

import java.security.MessageDigest;

public class Hasher {
    public static String hash(byte[] content){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(content);
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b)); // Format each byte as a two-character hex string
            }
            return hashString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return "";
    }
}
