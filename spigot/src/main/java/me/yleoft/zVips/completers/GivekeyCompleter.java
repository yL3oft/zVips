package me.yleoft.zVips.completers;

import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.VIPUtils;
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

public class GivekeyCompleter extends ConfigUtils implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (!(s instanceof Player) || !s.hasPermission(CmdGivekeyPermission()))
            return completions;
        Player p = (Player)s;
        if (args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(on -> commands.add(on.getName()));
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            VIPUtils.vipsCache.values().forEach(vip -> commands.add(vip.getName()));
            StringUtil.copyPartialMatches(args[1], commands, completions);
        } else if (args.length == 3) {
            commands.add("[duration]");
            StringUtil.copyPartialMatches(args[2], commands, completions);
        } else if (args.length == 4) {
            commands.add("[uses]");
            StringUtil.copyPartialMatches(args[3], commands, completions);
        }
        Collections.sort(commands);
        return completions;
    }

}
