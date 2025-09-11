package me.yleoft.zVips.completers;

import me.yleoft.zVips.utils.ConfigUtils;
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

public class TransferkeyCompleter extends ConfigUtils implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (!(s instanceof Player) || !s.hasPermission(CmdGivekeyPermission()))
            return completions;
        Player p = (Player)s;
        if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(on -> commands.add(on.getName()));
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(commands);
        return completions;
    }

}
