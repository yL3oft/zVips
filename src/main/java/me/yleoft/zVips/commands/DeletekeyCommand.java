package me.yleoft.zVips.commands;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeletekeyCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdDeletekeyPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }
        }

        LanguageUtils.Deletekey lang = new LanguageUtils.Deletekey();

        if(args.length == 1) {
            String idS = args[0];
            if(!StringUtils.isInteger(idS)) {
                lang.sendMsg(s, cmdm.getStringNotANumber());
                return false;
            }
            int id = Integer.parseInt(idS);
            KEY key = zVips.dbe.getKey(id);
            if(key == null) {
                lang.sendMsg(s, cmdm.getCantFindKEY());
                return false;
            }
            zVips.dbe.deleteKey(id);
            lang.sendMsg(s, lang.getOutput(key));
            return true;
        }

        lang.sendMsg(s, lang.getUsage());
        return false;
    }
}
