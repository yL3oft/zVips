package me.yleoft.zVips.constructors;

import me.yleoft.zAPI.managers.LogManager;
import me.yleoft.zAPI.utils.LogUtils;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.utils.KeysUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class KEY extends KeysUtils {

    private final String key;
    private int id;
    private VIP vip;
    private final String vipName;
    private final int uses;
    private long duration;

    private final OfflinePlayer owner;

    public KEY(String key, int id, VIP vip, String vipName, int uses, long duration, @Nullable OfflinePlayer owner) {
        this.key = key;
        this.id = id;
        this.vip = vip;
        this.vipName = vipName;
        this.uses = uses > 0 ? uses : 1;
        this.duration = duration;
        this.owner = owner;
    }

    public KEY(String key, VIP vip, long duration, int uses, @Nullable OfflinePlayer owner) {
        this(key, 0, vip, vip.getName(), uses, duration, owner);
    }
    public KEY(String key, VIP vip, long duration, @Nullable OfflinePlayer owner) {
        this(key, vip, duration, 1, owner);
    }
    public KEY(VIP vip, long duration, int uses, @Nullable OfflinePlayer owner) {
        this(KeysUtils.generateKey(), vip, duration, uses, owner);
    }

    public KEY(String key, int id, VIP vip, long duration, int uses, @Nullable OfflinePlayer owner) {
        this(key, id, vip, vip.getName(), uses, duration, owner);
    }
    public KEY(String key, int id, VIP vip, long duration, @Nullable OfflinePlayer owner) {
        this(key, id, vip, duration, 1, owner);
    }

    public KEY(String key, int id, VIP vip, int uses, @Nullable OfflinePlayer owner) {
        this(key, id, vip, vip.getName(), uses, vip.getDuration(), owner);
    }
    public KEY(String key, int id, VIP vip, @Nullable OfflinePlayer owner) {
        this(key, id, vip, 1, owner);
    }

    public KEY(String key, int id, String vipName, long duration, int uses, @Nullable OfflinePlayer owner) {
        this(key, id, null, vipName, uses, duration, owner);
    }
    public KEY(String key, int id, String vipName, long duration, @Nullable OfflinePlayer owner) {
        this(key, id, vipName, duration, 1, owner);
    }

    public KEY(String key, String vipName, long duration, int uses, @Nullable OfflinePlayer owner) {
        this(key, 0, null, vipName, uses, duration, owner);
    }
    public KEY(String key, String vipName, long duration, @Nullable OfflinePlayer owner) {
        this(key, vipName, duration, 1, owner);
    }

    public String getKey() {
        return key;
    }

    public int getID() {
        return id;
    }

    public VIP getVip() {
        if(vip == null && vipName != null) {
            vip = new VIP(vipName);
        }
        return vip;
    }

    public String getVipName() {
        return vipName;
    }

    public int getUses() {
        return uses;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public void saveKey(CommandSender s) {
        this.id = zVips.dbe.setKey(this);
        saveKeyLog(s);
    }
    public void saveKey() {
        saveKey(null);
    }

    private void saveKeyLog(CommandSender s) {
        if(enableLogs()) {
            LogUtils utils = LogManager.createFile(zVips.keygens);
            String message = ((s instanceof Player) ? s.getName() : "SERVER ") + " " +
                    "generated key " + this.getKey() + " with id " + this.getID() + " " +
                    "for vip " + this.getVipName() + " with duration " + this.getDuration() + " " +
                    "(" + StringUtils.parseAsString(this.getDuration()) + ") " +
                    "with " + this.getUses() + " uses for player " +
                    (getOwner() == null ? "SERVER" : getOwner().getName());
            utils.log(message);
        }
    }

}
