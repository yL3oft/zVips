package me.yleoft.zVips.commands;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.constructors.VIP;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.yleoft.zVips.utils.VIPUtils.vipsCache;

public class GivekeyCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdGivekeyPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }
        }

        LanguageUtils.Givekey lang = new LanguageUtils.Givekey();

        if(args.length >= 2) {
            OfflinePlayer t = PlayerUtils.getOfflinePlayer(args[0]);
            if(t == null || !t.hasPlayedBefore()) {
                lang.sendMsg(s, cmdm.getCantFindPlayer());
                return false;
            }
            String vipS = args[1];
            if(!vipsCache.containsKey(vipS)) {
                lang.sendMsg(s, cmdm.getCantFindVIP());
                return false;
            }
            VIP vip = new VIP(vipS);
            long duration = vip.getDuration();
            if(args.length >= 3) {
                String durationS = args[2];
                try {
                    duration = StringUtils.parseAsTime(durationS);
                }catch (Exception e) {
                    lang.sendMsg(s, cmdm.getInvalidDuration());
                    return false;
                }
            }
            int uses = 1;
            if(args.length >= 4) {
                String usesS = args[3];
                if(!StringUtils.isInteger(usesS)) {
                    lang.sendMsg(s, cmdm.getStringNotANumber());
                    return false;
                }
                uses = Integer.parseInt(usesS);
            }

            KEY key = new KEY(vip, duration, uses, t);
            key.saveKey(s);
            lang.sendMsg(s, lang.getOutput(cmdm, t, key));
            return true;
        }

        lang.sendMsg(s, lang.getUsage());
        return false;
    }
}
