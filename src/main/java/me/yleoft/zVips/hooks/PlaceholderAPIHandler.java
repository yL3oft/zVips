package me.yleoft.zVips.hooks;

import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.DPLAYER;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.zVips;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class PlaceholderAPIHandler extends me.yleoft.zAPI.handlers.PlaceholderAPIHandler {

    @Override
    public String applyHookPlaceholders(OfflinePlayer p, String params) {
        DPLAYER dp = null;
        if(p != null) dp = new DPLAYER(p);
        switch (params) {
            case "version":
                return zVips.getInstance().pluginVer;
            case "activated_vips":
                return String.valueOf(dp == null ? 0 : dp.getActivated());
            case "points":
                return String.valueOf(dp == null ? 0 : dp.getPoints());
            case "partyvip_progress":
                return String.valueOf(zVips.dbe.getPVPoints());
            case "partyvip_objective":
                return String.valueOf(zVips.cfgu.pvipObjective());
        }
        if(params.startsWith("vipkey")) {
            if(dp == null) return null;
            String[] split = params.split("_");
            if(split.length < 2) return "";
            if(!StringUtils.isInteger(split[1])) return "";
            int id = Integer.parseInt(split[1]);
            List<KEY> keys = dp.getKeys();
            KEY key = keys.get(id-1);
            if(key == null) return "";
            if(split.length >= 3) {
                    switch (split[2]) {
                    case "id":
                        return String.valueOf(key.getID());
                    case "group":
                        return key.getVipName();
                    case "duration":
                        if(split.length == 4 && split[3].equals("formatted")) {
                            return StringUtils.parseAsString(key.getDuration());
                        }
                        return String.valueOf(key.getDuration());
                    case "uses":
                        return String.valueOf(key.getUses());
                    case "owner":
                        return key.getOwner() == null ? "" : key.getOwner().getName();
                }
            }
            return key.getKey();
        }else if(params.startsWith("server_vipkey")) {
            String[] split = params.split("_");
            if(split.length < 3) return "";
            if(!StringUtils.isInteger(split[2])) return "";
            int id = Integer.parseInt(split[2]);
            KEY key = zVips.dbe.getKey(id);
            if(key == null) return "";
            if(split.length >= 4) {
                switch (split[3]) {
                    case "group":
                        return key.getVipName();
                    case "duration":
                        if(split.length == 5 && split[4].equals("formatted")) {
                            return StringUtils.parseAsString(key.getDuration());
                        }
                        return String.valueOf(key.getDuration());
                    case "uses":
                        return String.valueOf(key.getUses());
                    case "owner":
                        return key.getOwner() == null ? "" : key.getOwner().getName();
                }
            }
            return key.getKey();
        }
        return "";
    }

}
