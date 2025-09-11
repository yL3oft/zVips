package me.yleoft.zVips.commands;

import com.zvips.api.event.player.ExecuteMainCommandEvent;
import me.yleoft.zAPI.managers.FileManager;
import me.yleoft.zAPI.managers.LogManager;
import me.yleoft.zAPI.managers.PluginYAMLManager;
import me.yleoft.zAPI.utils.FileUtils;
import me.yleoft.zAPI.utils.LogUtils;
import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.DPLAYER;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.constructors.VIP;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.KeysUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.utils.VIPUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.yleoft.zVips.utils.LanguageUtils.loadzAPIMessages;

public class MainCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        String subcmd2;
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;

            if (!p.hasPermission(CmdMainPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }

            ExecuteMainCommandEvent event = new ExecuteMainCommandEvent(p);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }

        LanguageUtils.MainCMD lang = new LanguageUtils.MainCMD();
        LanguageUtils.MainCMD.MainReload lang2 = new LanguageUtils.MainCMD.MainReload();
        LanguageUtils.MainCMD.MainVersion lang3 = new LanguageUtils.MainCMD.MainVersion();
        LanguageUtils.MainCMD.MainHelp lang4 = new LanguageUtils.MainCMD.MainHelp();
        LanguageUtils.MainCMD.MainConverter lang5 = new LanguageUtils.MainCMD.MainConverter();

        //<editor-fold desc="Checks">
        if (args.length == 0) {
            lang.sendMsg(s, getUsage(s, lang4));
            return false;
        }
        //</editor-fold>

        String subcmd = args[0];
        switch (subcmd.toLowerCase()) {
            case "help":
            case "?":
                lang.sendMsg(s, getUsage(s, lang4));
                return false;
            case "reload":
            case "rl":
                //<editor-fold desc="Checks">
                if (p != null && !p.hasPermission(CmdMainReloadPermission())) {
                    lang.sendMsg(s, cmdm.getNoPermission());
                    return false;
                }
                //</editor-fold>
                if (args.length == 1) {
                    lang.sendMsg(s, lang2.getOutput(reload("all")));
                    return true;
                }
                subcmd2 = args[1];
                switch (subcmd2) {
                    case "all":
                        lang.sendMsg(s, lang2.getOutput(reload("all")));
                        return false;
                    case "commands":
                        lang.sendMsg(s, lang2.getOutputCommands(reload("commands")));
                        return false;
                    case "config":
                        lang.sendMsg(s, lang2.getOutputConfig(reload("config")));
                        return false;
                    case "languages":
                        lang.sendMsg(s, lang2.getOutputLanguages(reload("languages")));
                        return false;
                }
                lang.sendMsg(s, lang2.getUsage());
                return false;
            case "converter":
                if (p != null && !p.hasPermission(CmdMainConverterPermission())) {
                    lang.sendMsg(s, cmdm.getNoPermission());
                    return false;
                }
                if (args.length == 1) {
                    lang.sendMsg(s, lang5.getUsage());
                    return false;
                }
                zVips.db.migrateData(p, args[1]);
                return false;
            case "version":
            case "ver":
                if (p != null && !p.hasPermission(CmdMainVersionPermission())) {
                    lang.sendMsg(s, cmdm.getNoPermission());
                    return false;
                }
                lang.sendMsg(s, lang3.getOutput());
                return false;
            case "-debug":
            case "--debug":
                if (p != null && !p.hasPermission(CmdMainReloadPermission())) {
                    lang.sendMsg(s, cmdm.getNoPermission());
                    return false;
                }
                if(args.length > 1) {
                    subcmd2 = args[1].toLowerCase();
                    switch (subcmd2) {
                        case "genkey": {
                            lang.sendMsg(s, KeysUtils.generateKey());
                            break;
                        }
                        case "savekey": {
                            if (args.length < 4) {
                                return false;
                            }
                            String key = KeysUtils.generateKey();
                            String vipName = args[2];
                            long duration = StringUtils.parseAsTime(args[3]);
                            int uses = 1;
                            OfflinePlayer owner = null;
                            if(args.length >= 5) {
                                String subcmd5 = args[4];
                                if(StringUtils.isInteger(subcmd5)) {
                                    uses = Integer.parseInt(subcmd5);
                                    if(args.length == 6) {
                                        owner = PlayerUtils.getOfflinePlayer(args[5]);
                                    }
                                }else {
                                    owner = PlayerUtils.getOfflinePlayer(subcmd5);
                                }
                            }
                            KEY gkey = new KEY(key, vipName, duration, uses, owner);
                            int id = zVips.dbe.setKey(gkey);
                            KEY ckey = zVips.dbe.getKey(id);
                            lang.sendMsg(s, "&6[SAVE] Key Details:");
                            lang.sendMsg(s, "&6Key: &a" + ckey.getKey());
                            lang.sendMsg(s, "&6ID: &a" + ckey.getID());
                            lang.sendMsg(s, "&6Vip: &a" + ckey.getVipName());
                            lang.sendMsg(s, "&6Duration: &a" + args[3] + " &7(" + ckey.getDuration() + "ms)");
                            lang.sendMsg(s, "&6Uses: &a" + ckey.getUses());
                            lang.sendMsg(s, "&6Player: &a" + (owner == null ? "null" : owner.getName()));
                            break;
                        }
                        case "deletekey": {
                            if (args.length != 3) {
                                return false;
                            }
                            int id = Integer.parseInt(args[2]);
                            zVips.dbe.deleteKey(id);
                            break;
                        }
                        case "transferkey": {
                            if (args.length != 4) {
                                return false;
                            }
                            int id = Integer.parseInt(args[2]);
                            OfflinePlayer t = PlayerUtils.getOfflinePlayer(args[3]);
                            KEY keyO = zVips.dbe.getKey(id);
                            zVips.dbe.transferKey(id, t);
                            KEY key = zVips.dbe.getKey(id);
                            lang.sendMsg(s, "&b[TRANSFER] Key Details:");
                            lang.sendMsg(s, "&bKey: &a" + key.getKey());
                            lang.sendMsg(s, "&bID: &a" + key.getID());
                            lang.sendMsg(s, "&bVip: &a" + key.getVipName());
                            lang.sendMsg(s, "&bDuration: &a" + StringUtils.parseAsString(key.getDuration()) + " &7(" + key.getDuration() + "ms)");
                            lang.sendMsg(s, "&bUses: &a" + key.getUses());
                            lang.sendMsg(s, "&bPlayer: &a" + (keyO.getOwner() == null ? "null" : keyO.getOwner().getName()) + " -> " + (key.getOwner() == null ? "null" : key.getOwner().getName()));
                            break;
                        }
                        case "usekey": {
                            if (args.length != 4) {
                                return false;
                            }
                            OfflinePlayer t = PlayerUtils.getOfflinePlayer(args[2]);
                            DPLAYER target = new DPLAYER(t);
                            int points = target.getPoints();
                            int activated = target.getActivated();
                            int id = Integer.parseInt(args[3]);
                            KEY key = zVips.dbe.getKey(id);
                            target.useKey(key);
                            lang.sendMsg(s, "&d[USEKEY] Player Details:");
                            lang.sendMsg(s, "&dPlayer: &a" + target.getPlayer().getName());
                            lang.sendMsg(s, "&dPoints: &a" + points + " -> " + target.getPoints());
                            lang.sendMsg(s, "&dActivated: &a" + activated + " -> " + target.getActivated());
                            break;
                        }
                        case "getplayer": {
                            if (args.length != 3) {
                                return false;
                            }
                            OfflinePlayer t = PlayerUtils.getOfflinePlayer(args[2]);
                            DPLAYER target = new DPLAYER(t);
                            lang.sendMsg(s, "&d[GET] Player Details:");
                            lang.sendMsg(s, "&dPlayer: &a" + target.getPlayer().getName());
                            lang.sendMsg(s, "&dPoints: &a" + target.getPoints());
                            lang.sendMsg(s, "&dActivated: &a" + target.getActivated());
                            break;
                        }
                        case "getkey": {
                            if (args.length != 3) {
                                return false;
                            }
                            int id = Integer.parseInt(args[2]);
                            KEY key = zVips.dbe.getKey(id);
                            lang.sendMsg(s, "&2[GET] Key Details:");
                            lang.sendMsg(s, "&2Key: &a" + key.getKey());
                            lang.sendMsg(s, "&2ID: &a" + key.getID());
                            lang.sendMsg(s, "&2Vip: &a" + key.getVipName());
                            lang.sendMsg(s, "&2Duration: &a" + StringUtils.parseAsString(key.getDuration()) + " &7(" + key.getDuration() + "ms)");
                            lang.sendMsg(s, "&2Uses: &a" + key.getUses());
                            lang.sendMsg(s, "&2Player: &a" + (key.getOwner() == null ? "null" : key.getOwner().getName()));
                            break;
                        }
                        case "getvip": {
                            if (args.length != 3) {
                                return false;
                            }
                            VIP vip = VIPUtils.getVIP(args[2]);
                            lang.sendMsg(s, "&e(VIP) " + vip.getName());
                            lang.sendMsg(s, "&e- Display name: &a" + vip.getDisplayName());
                            lang.sendMsg(s, "&e- Duration: &a" + StringUtils.parseAsString(vip.getDuration()) + " &7(" + vip.getDuration() + "ms)");
                            lang.sendMsg(s, "&e- Points: &a" + vip.getPoints());
                            lang.sendMsg(s, "&e- Commands: &a" + vip.getCommands().toString());
                            break;
                        }
                        case "listkeys": {
                            OfflinePlayer t = null;
                            if (args.length >= 3) {
                                t = PlayerUtils.getOfflinePlayer(args[2]);
                            }
                            if (t == null) {
                                lang.sendMsg(s, "&9[LIST] All keys:");
                            } else {
                                if(args.length == 4 && args[3].equalsIgnoreCase("--usable")) {
                                    lang.sendMsg(s, "&9[LIST] " + t.getName() + "'s usable keys:");
                                    zVips.dbe.getUsableKeys(t).forEach(key -> lang.sendMsg(s, "&9- Key: &a" + key.getKey() + " &7(ID: " + key.getID() + " O: " + (key.getOwner() == null ? "false" : "true") + ")"));
                                    return true;
                                }
                                lang.sendMsg(s, "&9[LIST] " + t.getName() + "'s keys:");
                            }
                            zVips.dbe.getKeys(t).forEach(key -> lang.sendMsg(s, "&9- Key: &a" + key.getKey() + " &7(ID: " + key.getID() + ")"));
                            break;
                        }
                        case "listvips": {
                            HashMap<String, VIP> vips = VIPUtils.vipsCache;
                            lang.sendMsg(s, "&e[LIST] All vips:");
                            for(Map.Entry<String, VIP> entry : vips.entrySet()) {
                                String vipName = entry.getKey();
                                VIP vip = entry.getValue();
                                lang.sendMsg(s, "&e(VIP) " + vipName);
                                lang.sendMsg(s, "&e- Display name: &a" + vip.getDisplayName());
                                lang.sendMsg(s, "&e- Duration: &a" + StringUtils.parseAsString(vip.getDuration()) + " &7(" + vip.getDuration() + "ms)");
                                lang.sendMsg(s, "&e- Points: &a" + vip.getPoints());
                                lang.sendMsg(s, "&e- Commands: &a" + vip.getCommands().toString());
                            }
                            break;
                        }
                        case "testlog": {
                            if (args.length < 4) {
                                return false;
                            }
                            String log = args[2];
                            StringBuilder sb = new StringBuilder();
                            for (int i = 3; i < args.length; i++) {
                                sb.append(args[i]);
                                if (i < args.length - 1) {
                                    sb.append(" ");
                                }
                            }
                            LogUtils utils = LogManager.createFile(log);
                            utils.log(sb.toString());
                        }
                    }
                }
                return true;
        }
        lang.sendMsg(s, getUsage(s, lang4));
        return false;
    }

    public String getUsage(CommandSender s, LanguageUtils.MainCMD.MainHelp lang) {
        if (s instanceof Player) {
            Player p = (Player)s;
            if (p.hasPermission(CmdMainHelpPermission()))
                return lang.getUsageWithPerm();
            return lang.getUsage();
        }
        return lang.getUsageWithPerm();
    }

    public long reload(String which) {
        long now = System.currentTimeMillis();
        switch (which) {
            case "all":
                reload("commands");
                reload("languages");
                break;
            case "commands":
                reload("config");
                zVips.getInstance().loadCommands();
                PluginYAMLManager.syncCommands();
                break;
            case "config":
                zVips.getInstance().reloadConfig();
                zVips.cfgu = new ConfigUtils();
                break;
            case "languages":
                for(FileUtils fu : FileManager.getFiles()) {
                    fu.reloadConfig(false);
                }
                VIPUtils.vipsCache = VIPUtils.getVipsHashMap();
                loadzAPIMessages();
                break;
        }
        return System.currentTimeMillis() - now;
    }
}
