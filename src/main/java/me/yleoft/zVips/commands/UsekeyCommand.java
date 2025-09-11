package me.yleoft.zVips.commands;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.DPLAYER;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.constructors.VIP;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.yleoft.zVips.utils.VIPUtils.vipsCache;

public class UsekeyCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdUsekeyPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }
        }else {
            if(args.length < 2) {
                cmdm.sendMsg(s, cmdm.getOnlyExecutableByPlayers());
                return false;
            }
        }

        LanguageUtils.Usekey lang = new LanguageUtils.Usekey();

        if(args.length >= 1) {
            String keyT = args[0];
            KEY key = zVips.dbe.getKey(keyT);
            if(key == null) {
                lang.sendMsg(s, cmdm.getCantFindKEY());
                return false;
            }
            OfflinePlayer t;
            if(args.length == 2 && s.hasPermission(CmdUsekeyOthersPermission())) {
                t = PlayerUtils.getOfflinePlayer(args[1]);
                if (t == null || !t.hasPlayedBefore()) {
                    lang.sendMsg(s, cmdm.getCantFindPlayer());
                    return false;
                }
            }else t = p;
            DPLAYER dp = new DPLAYER(t);

            if(key.getOwner() == null || key.getOwner().getUniqueId().equals(dp.getPlayer().getUniqueId())) {
                dp.useKey(key);
                if(dp.getPlayer().getUniqueId() != p.getUniqueId()) {
                    lang.sendMsg(s, lang.getOutput(t, key));
                }
                return true;
            }

            lang.sendMsg(s, cmdm.getCantFindKEY());
            return false;
        }

        lang.sendMsg(s, lang.getUsage(s));
        return false;
    }
}
