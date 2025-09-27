package me.yleoft.zVips.constructors;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zVips.zVipsBukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class DPLAYER {

    private final OfflinePlayer p;
    private String vip;

    public DPLAYER(OfflinePlayer p) {
        this.p = p;
        this.vip = zVipsBukkit.dbe.getActive(p);
    }

    public OfflinePlayer getPlayer() {
        return p;
    }

    public void useKey(KEY key) {
        VIP vip = key.getVip();

        PlayerUtils.performCommand(p.isOnline() ? (Player) p : null, vip.getCommands());
        zVipsBukkit.dbe.decreaseUses(key);
        zVipsBukkit.dbe.addPVPoints(vip.getPoints());
        zVipsBukkit.dbe.addPoints(p, vip.getPoints());
        zVipsBukkit.dbe.increaseActivated(p);
    }

    public List<KEY> getKeys() {
        return zVipsBukkit.dbe.getKeys(p);
    }

    public String getVip() {
        return vip;
    }

    public int getPoints() {
        return zVipsBukkit.dbe.getPoints(p);
    }

    public int getActivated() {
        return zVipsBukkit.dbe.getCurrentActivated(p);
    }

    public void updateVip() {

    }

}
