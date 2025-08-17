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

public class GenkeyCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdGenkeyPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }
        }

        LanguageUtils.Genkey lang = new LanguageUtils.Genkey();

        if(args.length >= 1) {
            String vipS = args[0];
            if(!vipsCache.containsKey(vipS)) {
                lang.sendMsg(s, cmdm.getCantFindVIP());
                return false;
            }
            VIP vip = new VIP(vipS);
            long duration = vip.getDuration();
            if(args.length >= 2) {
                String durationS = args[1];
                try {
                    duration = StringUtils.parseAsTime(durationS);
                }catch (Exception e) {
                    lang.sendMsg(s, cmdm.getInvalidDuration());
                    return false;
                }
            }
            int uses = 1;
            if(args.length >= 3) {
                String usesS = args[2];
                if(!StringUtils.isInteger(usesS)) {
                    lang.sendMsg(s, cmdm.getStringNotANumber());
                    return false;
                }
                uses = Integer.parseInt(usesS);
            }

            KEY key = new KEY(vip, duration, uses, null);
            key.saveKey();
            lang.sendMsg(s, lang.getOutput(cmdm, key));
            return true;
        }

        lang.sendMsg(s, lang.getUsage());
        return false;
    }
}
