package me.yleoft.zVips.completers;

import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.VIPUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCompleter extends ConfigUtils implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (!(s instanceof Player))
            return completions;
        Player p = (Player)s;
        if (args.length == 1) {
            if (p.hasPermission(CmdMainReloadPermission()))
                commands.add("reload");
            if (p.hasPermission(CmdMainVersionPermission()))
                commands.add("version");
            if (p.hasPermission(CmdMainConverterPermission()))
                commands.add("converter");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            switch (args[0]) {
                case "reload":
                case "rl": {
                    if (p.hasPermission(CmdMainReloadPermission())) {
                        commands.add("all");
                        commands.add("commands");
                        commands.add("config");
                        commands.add("languages");
                    }
                    break;
                }
                case "converter": {
                    if (p.hasPermission(CmdMainConverterPermission())) {
                        commands.add("sqlitetoh2");
                        commands.add("sqlitetomysql");
                        commands.add("sqlitetomariadb");
                        commands.add("mysqltosqlite");
                        commands.add("mysqltoh2");
                        commands.add("mariadbtosqlite");
                        commands.add("mariadbtoh2");
                        commands.add("h2tosqlite");
                        commands.add("h2tomysql");
                        commands.add("h2tomariadb");
                    }
                    break;
                }
                case "-debug":
                case "--debug": {
                    if (p.hasPermission(CmdMainReloadPermission())) {
                        commands.add("genkey");
                        commands.add("savekey");
                        commands.add("deletekey");
                        commands.add("transferkey");
                        commands.add("usekey");
                        commands.add("getplayer");
                        commands.add("getkey");
                        commands.add("getvip");
                        commands.add("listkeys");
                        commands.add("listvips");
                        commands.add("testlog");
                    }
                    break;
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            if (p.hasPermission(CmdMainReloadPermission())) {
                switch (args[1]) {
                    case "listkeys":
                    case "getplayer":
                    case "usekey": {
                        Bukkit.getOnlinePlayers().forEach(on -> commands.add(on.getName()));
                        break;
                    }
                    case "getvip":
                    case "savekey": {
                        VIPUtils.vipsCache.values().forEach(vip -> commands.add(vip.getName()));
                        break;
                    }
                    case "testlog": {
                        commands.add("(log file)");
                    }
                }
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        } else if (args.length == 4) {
            if (p.hasPermission(CmdMainReloadPermission())) {
                switch (args[1]) {
                    case "transferkey": {
                        Bukkit.getOnlinePlayers().forEach(on -> commands.add(on.getName()));
                        break;
                    }
                    case "listkeys": {
                        commands.add("--usable");
                        break;
                    }
                    case "savekey": {
                        commands.add("1y");
                        commands.add("3mo");
                        commands.add("30d");
                        commands.add("7d");
                        commands.add("3d");
                        commands.add("1h");
                    }
                    case "testlog": {
                        commands.add("(message)");
                    }
                }
            }
            StringUtil.copyPartialMatches(args[3], commands, completions);
        } else if (args.length == 5) {
            if (p.hasPermission(CmdMainReloadPermission())) {
                if (args[1].equals("savekey")) {
                    commands.add("<uses>");
                    Bukkit.getOnlinePlayers().forEach(on -> commands.add(on.getName()));
                }
            }
            StringUtil.copyPartialMatches(args[4], commands, completions);
        } else if (args.length == 6) {
            if (p.hasPermission(CmdMainReloadPermission())) {
                if (args[1].equals("savekey")) {
                    Bukkit.getOnlinePlayers().forEach(on -> commands.add(on.getName()));
                }
            }
            StringUtil.copyPartialMatches(args[5], commands, completions);
        }
        Collections.sort(commands);
        return completions;
    }

}
