package me.yleoft.zVips.commands;

import com.zvips.api.event.BalanceChangeType;
import com.zvips.api.event.partyvip.PartyvipProgressChangeEvent;
import com.zvips.api.event.player.ExecutePartyvipCommandEvent;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.yleoft.zAPI.utils.StringUtils.isInteger;

public class PartyvipCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdPartyvipPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }

            ExecutePartyvipCommandEvent event = new ExecutePartyvipCommandEvent(p);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }

        LanguageUtils.Partyvip lang = new LanguageUtils.Partyvip();

        if(args.length > 0) {
            if (s.hasPermission(CmdPointsvipManagePermission())) {
                String subcmd = args[0].toLowerCase();
                switch (subcmd) {
                    case "add":
                    case "adicionar": {
                        if(args.length != 2) {
                            lang.sendMsg(s, lang.getUsage());
                            return false;
                        }
                        if(!isInteger(args[1])) {
                            lang.sendMsg(s, cmdm.getStringNotANumber());
                            return false;
                        }
                        int points = Integer.parseInt(args[1]);

                        PartyvipProgressChangeEvent event = new PartyvipProgressChangeEvent(points, BalanceChangeType.ADD);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.addPVPoints(points);
                        lang.sendMsg(s, lang.getAdd(points));
                        break;
                    }
                    case "remove":
                    case "remover": {
                        if(args.length != 2) {
                            lang.sendMsg(s, lang.getUsage());
                            return false;
                        }
                        if(!isInteger(args[1])) {
                            lang.sendMsg(s, cmdm.getStringNotANumber());
                            return false;
                        }
                        int points = Integer.parseInt(args[1]);

                        PartyvipProgressChangeEvent event = new PartyvipProgressChangeEvent(points, BalanceChangeType.REMOVE);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.removePVPoints(points);
                        lang.sendMsg(s, lang.getRemove(points));
                        break;
                    }
                    case "set":
                    case "definir": {
                        if(args.length != 2) {
                            lang.sendMsg(s, lang.getUsage());
                            return false;
                        }
                        if(!isInteger(args[1])) {
                            lang.sendMsg(s, cmdm.getStringNotANumber());
                            return false;
                        }
                        int points = Integer.parseInt(args[1]);

                        PartyvipProgressChangeEvent event = new PartyvipProgressChangeEvent(points, BalanceChangeType.SET);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.setPVPoints(points);
                        lang.sendMsg(s, lang.getSet(points));
                        break;
                    }
                    case "reset":
                    case "redefinir": {
                        PartyvipProgressChangeEvent event = new PartyvipProgressChangeEvent(0, BalanceChangeType.RESET);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.resetPVPoints();
                        lang.sendMsg(s, lang.getReset());
                        break;
                    }
                    default:
                        lang.sendMsg(s, lang.getUsage());
                        return false;
                }
                return true;
            }
        }

        lang.sendMsg(s, lang.getOutput());

        return false;
    }
}
