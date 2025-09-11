package me.yleoft.zVips.utils;

import me.yleoft.zAPI.managers.FileManager;
import me.yleoft.zAPI.utils.FileUtils;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.VIP;
import me.yleoft.zVips.zVips;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class VIPUtils extends ConfigUtils {

    public static String vipsFolder = "vips/";
    public static File vipsDir = new File(zVips.getInstance().getDataFolder(), "vips");

    public static HashMap<String, VIP> vipsCache = getVipsHashMap();

    public static List<VIP> getVips() {
        List<VIP> vips = new ArrayList<>();
        if(vipsDir.exists() && vipsDir.isDirectory()) {
            File[] files = vipsDir.listFiles((dir, name) -> name.endsWith(".yml"));
            List<FileUtils> fus = new ArrayList<>();
            if (files != null) {
                for (File file : files) {
                    fus.add(FileManager.createFile(vipsFolder+file.getName()));
                }
            }
            fus.forEach(fu -> vips.add(new VIP(fu)));
        }
        return vips;
    }

    public static HashMap<String, VIP> getVipsHashMap() {
        HashMap<String, VIP> vipsMap = new HashMap<>();
        for (VIP vip : getVips()) {
            vipsMap.put(vip.getName(), vip);
        }
        return vipsMap;
    }

    public static VIP getVIP(@NotNull String name) {
        if (vipsCache.containsKey(name)) {
            return vipsCache.get(name);
        }
        throw new IllegalArgumentException("VIP with name '" + name + "' not found.");
    }

    public static String getDisplayName(@NotNull YamlConfiguration config) {
        String path = "displayname";
        if (config.contains(path) && config.isString(path)) {
            return config.getString(path);
        }
        return null;
    }

    public static long getDuration(@NotNull YamlConfiguration config) {
        String path = "default-duration";
        if (config.contains(path) && config.isString(path)) {
            String durationString = config.getString(path);
            try {
                return StringUtils.parseAsTime(durationString);
            } catch (NumberFormatException | NullPointerException e) {
                throw new IllegalArgumentException("Path '" + path + "' is not a valid duration in the configuration: " + durationString, e);
            }
        } else if (config.contains(path) && config.isLong(path)) {
            return config.getLong(path) * 1000L;
        }
        throw new IllegalArgumentException("Path '" + path + "' is not a valid duration in the configuration.");
    }

    public static int getPoints(@NotNull YamlConfiguration config) {
        String path = "points";
        if (config.contains(path) && config.isInt(path)) {
            return config.getInt(path);
        }
        return 0;
    }

    public static List<String> getCommands(@NotNull YamlConfiguration config, @NotNull String path) {
        if(config.isList(path)) {
            return config.getStringList(path);
        } else if (config.isString(path)) {
            return Collections.singletonList(config.getString(path));
        }
        return new ArrayList<>();
    }

}
