package me.yleoft.zVips.commands;

import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.zVipsBukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ListkeysCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdListkeysPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }
        }

        LanguageUtils.Listkeys lang = new LanguageUtils.Listkeys();

        OfflinePlayer t = null;
        if(args.length >= 1 && (p == null || p.hasPermission(CmdListkeysOthersPermission()))) {
            if(args[0].equalsIgnoreCase("server")) {
                lang.sendMsg(s, lang.getOutput(s, null, zVipsBukkit.dbe.getKeys()));
                return true;
            }
            t = PlayerUtils.getOfflinePlayer(args[0]);
            if(t == null || !t.hasPlayedBefore()) {
                lang.sendMsg(s, cmdm.getCantFindPlayer());
                return false;
            }
            lang.sendMsg(s, lang.getOutput(s, t, zVipsBukkit.dbe.getKeys(t)));
            return true;
        }
        lang.sendMsg(s, lang.getOutputSelf(p, zVipsBukkit.dbe.getKeys(p)));
        return false;
    }
}
