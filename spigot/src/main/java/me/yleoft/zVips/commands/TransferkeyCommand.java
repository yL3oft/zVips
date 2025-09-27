package me.yleoft.zVips.commands;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.zVipsBukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TransferkeyCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdTransferkeyPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }
        }

        LanguageUtils.Transferkey lang = new LanguageUtils.Transferkey();

        if(args.length == 2) {
            String idS = args[0];
            if(!StringUtils.isInteger(idS)) {
                lang.sendMsg(s, cmdm.getStringNotANumber());
                return false;
            }
            int id = Integer.parseInt(idS);
            OfflinePlayer t = PlayerUtils.getOfflinePlayer(args[1]);
            if(t == null || !t.hasPlayedBefore()) {
                lang.sendMsg(s, cmdm.getCantFindPlayer());
                return false;
            }

            KEY key = zVipsBukkit.dbe.getKey(id);
            if(key == null) {
                lang.sendMsg(s, cmdm.getCantFindKEY());
                return false;
            }
            zVipsBukkit.dbe.transferKey(id, t);
            lang.sendMsg(s, lang.getOutput(t, key));
            return true;
        }

        lang.sendMsg(s, lang.getUsage());
        return false;
    }
}
