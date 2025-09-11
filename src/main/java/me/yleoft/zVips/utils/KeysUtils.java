package me.yleoft.zVips.utils;

import com.zvips.api.exceptions.FailedKeyGeneration;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.constructors.VIP;
import me.yleoft.zVips.zVips;

import java.security.SecureRandom;

public abstract class KeysUtils extends ConfigUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateKey(int regenerations) {
        if(regenerations > 10) {
            throw new FailedKeyGeneration("Failed to generate a unique GKEY after 10 attempts.");
        }
        int length = 8 + RANDOM.nextInt(5);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        String gkey = sb.toString();
        return zVips.dbe.isInTable(zVips.cfgu.databaseTable(), "GKEY", gkey) ? generateKey(regenerations+1) : gkey;
    }
    public static String generateKey() {
        return generateKey(0);
    }

    public static String parseString(String text, KEY key) {
        return text
                .replace("%key%", key.getKey())
                .replace("%id%", String.valueOf(key.getID()))
                .replace("%vip%", key.getVipName())
                .replace("%duration%", String.valueOf(key.getDuration()))
                .replace("%duration_formatted%", StringUtils.parseAsString(key.getDuration()))
                .replace("%uses%", String.valueOf(key.getUses()))
                .replace("%owner%", key.getOwner() == null ? "null" : key.getOwner().getName());
    }

}
