package me.yleoft.zVips.constructors;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class DPLAYER {

    private final OfflinePlayer p;
    private String vip;

    public DPLAYER(OfflinePlayer p) {
        this.p = p;
        this.vip = zVips.dbe.getActive(p);
    }

    public OfflinePlayer getPlayer() {
        return p;
    }

    public void useKey(KEY key) {
        VIP vip = key.getVip();

        PlayerUtils.performCommand(p.isOnline() ? (Player) p : null, vip.getCommands());
        zVips.dbe.decreaseUses(key);
        zVips.dbe.addPVPoints(vip.getPoints());
        zVips.dbe.addPoints(p, vip.getPoints());
        zVips.dbe.increaseActivated(p);
    }

    public List<KEY> getKeys() {
        return zVips.dbe.getKeys(p);
    }

    public String getVip() {
        return vip;
    }

    public int getPoints() {
        return zVips.dbe.getPoints(p);
    }

    public int getActivated() {
        return zVips.dbe.getCurrentActivated(p);
    }

    public void updateVip() {

    }

}
