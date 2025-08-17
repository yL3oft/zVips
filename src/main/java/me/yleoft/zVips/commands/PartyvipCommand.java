package me.yleoft.zVips.commands;

import com.zvips.api.event.player.BalanceChangeType;
import com.zvips.api.event.player.ExecutePointsvipCommandEvent;
import com.zvips.api.event.player.PlayerBalanceChangeEvent;
import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.yleoft.zAPI.utils.StringUtils.isInteger;

public class PointsvipCommand extends ConfigUtils implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = null;
        LanguageUtils.CommandsMSG cmdm = new LanguageUtils.CommandsMSG();
        if (s instanceof Player) {
            p = (Player) s;
            if (!p.hasPermission(CmdPointsvipPermission())) {
                cmdm.sendMsg(p, cmdm.getNoPermission());
                return false;
            }

            ExecutePointsvipCommandEvent event = new ExecutePointsvipCommandEvent(p);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
        }

        LanguageUtils.Pointsvip lang = new LanguageUtils.Pointsvip();

        if(args.length > 0) {
            OfflinePlayer t = PlayerUtils.getOfflinePlayer(args[0]);
            if(t != null && t.hasPlayedBefore()) {
                if(s.hasPermission(CmdPointsvipOthersPermission())) {
                    lang.sendMsg(s, lang.getOutputOthers(t));
                    return true;
                }
            }
            if (s.hasPermission(CmdPointsvipManagePermission())) {
                if(args.length < 2) {
                    lang.sendMsg(s, lang.getUsage());
                    return false;
                }
                String subcmd = args[0].toLowerCase();
                t = PlayerUtils.getOfflinePlayer(args[1]);
                if(t == null || !t.hasPlayedBefore()) {
                    lang.sendMsg(s, cmdm.getCantFindPlayer());
                    return false;
                }
                switch (subcmd) {
                    case "add":
                    case "adicionar": {
                        if(args.length != 3) {
                            lang.sendMsg(s, lang.getUsage());
                            return false;
                        }
                        if(!isInteger(args[2])) {
                            lang.sendMsg(s, cmdm.getStringNotANumber());
                            return false;
                        }
                        int points = Integer.parseInt(args[2]);

                        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(p, points, BalanceChangeType.ADD);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.addPoints(t, points);
                        lang.sendMsg(s, lang.getAdd(t, points));
                        break;
                    }
                    case "remove":
                    case "remover": {
                        if(args.length != 3) {
                            lang.sendMsg(s, lang.getUsage());
                            return false;
                        }
                        if(!isInteger(args[2])) {
                            lang.sendMsg(s, cmdm.getStringNotANumber());
                            return false;
                        }
                        int points = Integer.parseInt(args[2]);

                        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(p, points, BalanceChangeType.REMOVE);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.removePoints(t, points);
                        lang.sendMsg(s, lang.getRemove(t, points));
                        break;
                    }
                    case "set":
                    case "definir": {
                        if(args.length != 3) {
                            lang.sendMsg(s, lang.getUsage());
                            return false;
                        }
                        if(!isInteger(args[2])) {
                            lang.sendMsg(s, cmdm.getStringNotANumber());
                            return false;
                        }
                        int points = Integer.parseInt(args[2]);

                        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(p, points, BalanceChangeType.SET);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.setPoints(t, points);
                        lang.sendMsg(s, lang.getSet(t, points));
                        break;
                    }
                    case "reset":
                    case "redefinir": {
                        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(p, 0, BalanceChangeType.RESET);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) return false;

                        zVips.dbe.resetPoints(t);
                        lang.sendMsg(s, lang.getReset(t));
                        break;
                    }
                    default:
                        lang.sendMsg(s, lang.getUsage());
                        return false;
                }
                return true;
            }
        }

        if(p != null) {
            lang.sendMsg(p, lang.getOutput(p));
            return true;
        } else {
            lang.sendMsg(s, lang.getUsage());
        }

        return false;
    }
}
