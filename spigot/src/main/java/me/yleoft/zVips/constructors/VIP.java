package me.yleoft.zVips.constructors;

import me.yleoft.zAPI.managers.FileManager;
import me.yleoft.zAPI.utils.FileUtils;
import me.yleoft.zVips.utils.VIPUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class VIP extends VIPUtils {

    private final String name;
    private final String displayname;
    private long duration;
    private int points;
    private final List<String> commands;

    public VIP(File f, YamlConfiguration config) {
        try {
            name = f.getName().replace(".yml", "");
            displayname = getDisplayName(config) == null ? name : getDisplayName(config);
            duration = getDuration(config);
            points = getPoints(config);
            commands = getCommands(config, "activation-commands");
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error initializing VIP from configuration: " + e.getMessage(), e);
        }
    }
    public VIP(FileUtils fu) {
        this(fu.getFile(), (YamlConfiguration) fu.getConfig());
    }
    public VIP(String vipName) {
        this(FileManager.createFile(vipsFolder+vipName+".yml"));
    }
    public VIP(String name, String displayname, long duration, List<String> commands) {
        this.name = name;
        this.displayname = displayname;
        this.duration = duration;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayname;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<String> getCommands() {
        return commands;
    }

}
