package me.yleoft.zVips.managers;

import me.yleoft.zVips.utils.ConfigUtils;

import java.security.SecureRandom;

public abstract class KeysManager extends ConfigUtils {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateKey() {
        int length = 8 + RANDOM.nextInt(5); // 8 to 12
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

}
