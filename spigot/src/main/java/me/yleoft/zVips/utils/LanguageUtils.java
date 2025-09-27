package me.yleoft.zVips.utils;

import me.yleoft.zAPI.managers.FileManager;
import me.yleoft.zAPI.mutable.Messages;
import me.yleoft.zAPI.utils.FileUtils;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.zVipsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.yleoft.zAPI.utils.ConfigUtils.formPath;
import static me.yleoft.zAPI.utils.StringUtils.transform;

public class LanguageUtils extends ConfigUtils {

    private static final zVipsBukkit main = zVipsBukkit.getInstance();
    private static FileUtils fuBACKUP = null;

    public static String hooks = "hooks";
    public static String cmds = "commands";
    public static String vault = "vault";

    public static File f = new File(main.getDataFolder(), "languages/en.yml");

    public static YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);

    public LanguageUtils() {
        cfg = getConfigFile();
    }

    public static YamlConfiguration getConfigFile() {
        List<FileUtils> list = new ArrayList<>();
        list.add(FileManager.getFileUtil("languages/en.yml"));
        list.add(FileManager.getFileUtil("languages/pt-br.yml"));
        list.add(fuBACKUP);
        boolean found = false;
        YamlConfiguration returned = cfg;
        String lang = langType();
        for (FileUtils fu : list) {
            if (fu != null) {
                File f = fu.getFile();
                if (f.exists()) {
                    String name = f.getName();
                    String[] nameS = name.split("\\.");
                    String langtype = nameS[0];
                    if (langtype.equals(lang)) {
                        returned = (YamlConfiguration)fu.getConfig();
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found) {
            String resource = "languages/" + lang + ".yml";
            File f = new File(main.getDataFolder(), resource);
            if (f.exists()) {
                fuBACKUP = new FileUtils(f, resource);
                returned = (YamlConfiguration)fuBACKUP.getConfig();
            }
        }
        return returned;
    }

    public static class MainCMD implements Commands {
        public YamlConfiguration cfg;

        public MainCMD() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "main";
        }

        public String getUsage() {
            return null;
        }

        public String getOutput() {
            return null;
        }

        public static class MainHelp implements Commands {
            public YamlConfiguration cfg;

            public MainHelp() {
                this.cfg = LanguageUtils.getConfigFile();
            }

            public String getCmd() {
                return "main.help";
            }

            public String getUsage() {
                String path = formPath(cmds, getCmd(), "help-noperm");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand());
            }

            public String getUsageWithPerm() {
                String path = formPath(cmds, getCmd(), "help-perm");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand());
            }

            public String getOutput() {
                String path = formPath(cmds, getCmd(), "output");
                return this.cfg.getString(path)
                        .replace("%version%", zVipsBukkit.getInstance().pluginVer);
            }
        }

        public static class MainVersion implements Commands {
            public YamlConfiguration cfg;

            public MainVersion() {
                this.cfg = LanguageUtils.getConfigFile();
            }

            public String getCmd() {
                return "main.version";
            }

            public String getUsage() {
                return null;
            }

            public String getOutput() {
                String path = formPath(cmds, getCmd(), "output");
                return this.cfg.getString(path)
                        .replace("%version%", zVipsBukkit.getInstance().pluginVer);
            }
        }

        public static class MainReload implements Commands {
            public YamlConfiguration cfg;

            public MainReload() {
                this.cfg = LanguageUtils.getConfigFile();
            }

            public String getCmd() {
                return "main.reload";
            }

            public String getUsage() {
                String path = formPath(cmds, getCmd(), "usage");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand());
            }

            public String getOutput() {
                return null;
            }

            public String getOutput(long time) {
                String path = formPath(cmds, getCmd(), "output");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand())
                        .replace("%time%", String.valueOf(time));
            }

            public String getOutputCommands(long time) {
                String path = formPath(cmds, getCmd(), "commands.output");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand())
                        .replace("%time%", String.valueOf(time));
            }

            public String getOutputConfig(long time) {
                String path = formPath(cmds, getCmd(), "config.output");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand())
                        .replace("%time%", String.valueOf(time));
            }

            public String getOutputLanguages(long time) {
                String path = formPath(cmds, getCmd(), "languages.output");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand())
                        .replace("%time%", String.valueOf(time));
            }
        }

        public static class MainConverter implements LanguageUtils.Commands {
            public final YamlConfiguration cfg;

            public MainConverter() {
                this.cfg = LanguageUtils.getConfigFile();
            }

            public String getCmd() {
                return "main.converter";
            }

            public String getUsage() {
                String path = formPath(cmds, getCmd(), "usage");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand());
            }

            public String getOutput() {
                String path = formPath(cmds, getCmd(), "output");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand());
            }

            public String getError() {
                String path = formPath(cmds, getCmd(), "error");
                return this.cfg.getString(path)
                        .replace("%command%", zVipsBukkit.cfgu.CmdMainCommand());
            }
        }
    }

    public static class Pointsvip implements Commands {
        public final YamlConfiguration cfg;

        public Pointsvip() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "pointsvip";
        }

        public String getOutput() {
            return null;
        }

        public String getUsage() {
            String path = formPath(cmds, getCmd(), "usage");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand());
        }

        public String getOutput(Player p) {
            String path = formPath(cmds, getCmd(), "output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%points%", String.valueOf(zVipsBukkit.dbe.getPoints(p)));
        }

        public String getOutputOthers(OfflinePlayer p) {
            String path = formPath(cmds, getCmd(), "others.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%player%", Objects.requireNonNull(p.getName()))
                    .replace("%points%", String.valueOf(zVipsBukkit.dbe.getPoints(p)));
        }

        public String getSet(OfflinePlayer p, int points) {
            String path = formPath(cmds, getCmd(), "set.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%player%", Objects.requireNonNull(p.getName()))
                    .replace("%points%", String.valueOf(points));
        }

        public String getAdd(OfflinePlayer p, int points) {
            String path = formPath(cmds, getCmd(), "add.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%player%", Objects.requireNonNull(p.getName()))
                    .replace("%points%", String.valueOf(points));
        }

        public String getRemove(OfflinePlayer p, int points) {
            String path = formPath(cmds, getCmd(), "remove.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%player%", Objects.requireNonNull(p.getName()))
                    .replace("%points%", String.valueOf(points));
        }

        public String getReset(OfflinePlayer p) {
            String path = formPath(cmds, getCmd(), "reset.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%player%", Objects.requireNonNull(p.getName()));
        }
    }

    public static class Partyvip implements Commands {
        public final YamlConfiguration cfg;

        public Partyvip() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "partyvip";
        }

        public String getUsage() {
            String path = formPath(cmds, getCmd(), "usage");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand());
        }

        public String getOutput() {
            String path = formPath(cmds, getCmd(), "output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%points%", String.valueOf(zVipsBukkit.dbe.getPVPoints()))
                    .replace("%goal%", String.valueOf(zVipsBukkit.cfgu.pvipObjective()));
        }

        public String getSet(int points) {
            String path = formPath(cmds, getCmd(), "set.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%points%", String.valueOf(points))
                    .replace("%progress%", String.valueOf(zVipsBukkit.dbe.getPVPoints()))
                    .replace("%goal%", String.valueOf(zVipsBukkit.cfgu.pvipObjective()));
        }

        public String getAdd(int points) {
            String path = formPath(cmds, getCmd(), "add.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%points%", String.valueOf(points))
                    .replace("%progress%", String.valueOf(zVipsBukkit.dbe.getPVPoints()))
                    .replace("%goal%", String.valueOf(zVipsBukkit.cfgu.pvipObjective()));
        }

        public String getRemove(int points) {
            String path = formPath(cmds, getCmd(), "remove.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%points%", String.valueOf(points))
                    .replace("%progress%", String.valueOf(zVipsBukkit.dbe.getPVPoints()))
                    .replace("%goal%", String.valueOf(zVipsBukkit.cfgu.pvipObjective()));
        }

        public String getReset() {
            String path = formPath(cmds, getCmd(), "reset.output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdPointsvipCommand())
                    .replace("%progress%", String.valueOf(zVipsBukkit.dbe.getPVPoints()))
                    .replace("%goal%", String.valueOf(zVipsBukkit.cfgu.pvipObjective()));
        }
    }

    public static class Givekey implements Commands {
        public final YamlConfiguration cfg;

        public Givekey() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "givekey";
        }

        public String getOutput() {
            return null;
        }

        public String getUsage() {
            String path = formPath(cmds, getCmd(), "usage");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdGivekeyCommand());
        }

        public String getOutput(CommandsMSG cmdm, OfflinePlayer p, KEY key) {
            String path = formPath(cmds, getCmd(), "output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdGivekeyCommand())
                    .replace("%player%", p.getName())
                    .replace("%key-information%", cmdm.getKeyInformation(key));
        }
    }

    public static class Genkey implements Commands {
        public final YamlConfiguration cfg;

        public Genkey() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "genkey";
        }

        public String getOutput() {
            return null;
        }

        public String getUsage() {
            String path = formPath(cmds, getCmd(), "usage");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdGenkeyCommand());
        }

        public String getOutput(CommandsMSG cmdm, KEY key) {
            String path = formPath(cmds, getCmd(), "output");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdGenkeyCommand())
                    .replace("%key-information%", cmdm.getKeyInformation(key));
        }
    }

    public static class Listkeys implements Commands {
        public final YamlConfiguration cfg;

        public Listkeys() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "listkeys";
        }

        public String getOutput() {
            return null;
        }

        public String getUsage() {
            return null;
        }

        public String getOutput(CommandSender s, @Nullable OfflinePlayer p, List<KEY> keys) {
            String path = p == null ? formPath(cmds, getCmd(), "output") : formPath(cmds, getCmd(), "output-player");
            String path2 = p == null ? formPath(cmds, getCmd(), "server-keys")
                    : s.hasPermission(zVipsBukkit.cfgu.CmdListkeysOthersPermission()) ? formPath(cmds, getCmd(), "player-keys-admin") : formPath(cmds, getCmd(), "player-keys");
            StringBuilder keysString = new StringBuilder();
            for(KEY key : keys) {
                if(keysString.length() == 0) {
                    keysString = new StringBuilder(KeysUtils.parseString(this.cfg.getString(path2), key));
                    continue;
                }
                keysString.append("\n").append(KeysUtils.parseString(this.cfg.getString(path2), key));
            }
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdListkeysCommand())
                    .replace("%player%", p == null ? "" : p.getName())
                    .replace("%keys%", keysString.toString());
        }

        public String getOutputSelf(Player p, List<KEY> keys) {
            String path = formPath(cmds, getCmd(), "output-you");
            String path2 = p.hasPermission(zVipsBukkit.cfgu.CmdListkeysOthersPermission()) ? formPath(cmds, getCmd(), "player-keys-admin") : formPath(cmds, getCmd(), "player-keys");
            StringBuilder keysString = new StringBuilder();
            for(KEY key : keys) {
                if(keysString.length() == 0) {
                    keysString = new StringBuilder(KeysUtils.parseString(this.cfg.getString(path2), key));
                    continue;
                }
                keysString.append("\n").append(KeysUtils.parseString(this.cfg.getString(path2), key));
            }
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdListkeysCommand())
                    .replace("%keys%", keysString.toString());
        }
    }

    public static class Usekey implements Commands {
        public final YamlConfiguration cfg;

        public Usekey() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "usekey";
        }

        public String getOutput() {
            return null;
        }

        public String getUsage() {
            return null;
        }

        public String getUsage(CommandSender s) {
            String path = s.hasPermission(zVipsBukkit.cfgu.CmdUsekeyOthersPermission()) ? formPath(cmds, getCmd(), "usage-admin") : formPath(cmds, getCmd(), "usage");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdUsekeyCommand());
        }

        public String getOutput(OfflinePlayer p, KEY key) {
            String path = formPath(cmds, getCmd(), "output-admin");
            return KeysUtils.parseString(
                    this.cfg.getString(path)
                            .replace("%command%", zVipsBukkit.cfgu.CmdTransferkeyCommand())
                            .replace("%player%", p.getName())
                    , key);
        }
    }

    public static class Transferkey implements Commands {
        public final YamlConfiguration cfg;

        public Transferkey() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "transferkey";
        }

        public String getOutput() {
            return null;
        }

        public String getUsage() {
            String path = formPath(cmds, getCmd(), "usage");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdTransferkeyCommand());
        }

        public String getOutput(OfflinePlayer p, KEY key) {
            String path = formPath(cmds, getCmd(), "output");
            return KeysUtils.parseString(
                    this.cfg.getString(path)
                            .replace("%command%", zVipsBukkit.cfgu.CmdTransferkeyCommand())
                            .replace("%player%", p.getName())
                    , key);
        }
    }

    public static class Deletekey implements Commands {
        public final YamlConfiguration cfg;

        public Deletekey() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getCmd() {
            return "deletekey";
        }

        public String getOutput() {
            return null;
        }

        public String getUsage() {
            String path = formPath(cmds, getCmd(), "usage");
            return this.cfg.getString(path)
                    .replace("%command%", zVipsBukkit.cfgu.CmdDeletekeyCommand());
        }

        public String getOutput(KEY key) {
            String path = formPath(cmds, getCmd(), "output");
            return KeysUtils.parseString(
                    this.cfg.getString(path)
                            .replace("%command%", zVipsBukkit.cfgu.CmdDeletekeyCommand())
                    , key);
        }
    }

    public static class HooksMSG implements Helper {
        public YamlConfiguration cfg;

        public HooksMSG() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getVaultCantAfford(Float cost) {
            String path = formPath(hooks, vault, "cant-afford-command");
            return this.cfg.getString(path)
                    .replace("%cost%", Float.toString(cost));
        }
    }

    public static class CommandsMSG implements Helper {
        public YamlConfiguration cfg;

        public CommandsMSG() {
            this.cfg = LanguageUtils.getConfigFile();
        }

        public String getNoPermission() {
            String path = formPath(cmds, "no-permission");
            return this.cfg.getString(path);
        }

        public String getCantFindPlayer() {
            String path = formPath(cmds, "cant-find-player");
            return this.cfg.getString(path);
        }

        public String getCantFindVIP() {
            String path = formPath(cmds, "cant-find-vip");
            return this.cfg.getString(path);
        }

        public String getCantFindKEY() {
            String path = formPath(cmds, "cant-find-key");
            return this.cfg.getString(path);
        }

        public String getOnlyExecutableByPlayers() {
            String path = formPath(cmds, "only-executable-by-players");
            return this.cfg.getString(path);
        }

        public String getStringNotANumber() {
            String path = formPath(cmds, "string-not-a-number");
            return this.cfg.getString(path);
        }

        public String getInvalidDuration() {
            String path = formPath(cmds, "invalid-duration");
            return this.cfg.getString(path);
        }

        public String getKeyInformation(KEY key) {
            String keyinfopath = "key-information";
            String message = this.cfg.getString(formPath(cmds, keyinfopath, "message"))
                    .replace("%1color%", this.cfg.getString(formPath(cmds, keyinfopath, "1color")))
                    .replace("%2color%", this.cfg.getString(formPath(cmds, keyinfopath, "2color")));
            return KeysUtils.parseString(message, key);
        }
    }

    public static void loadzAPIMessages() {
        YamlConfiguration config = LanguageUtils.getConfigFile();
        Messages.setCooldownExpired(Objects.requireNonNull(Helper.getText(config.getString(formPath(cmds, "in-cooldown")))));
    }

    public interface Commands extends Helper {
        String getCmd();

        String getUsage();

        String getOutput();
    }

    public interface Helper {
        default void sendMsg(Player p, String text) {
            if(!p.isOnline()) return;
            if(text.isEmpty()) return;
            text = getText(p, text);
            p.sendMessage(text);
        }

        default void sendMsg(CommandSender s, String text) {
            if(text.isEmpty()) return;
            text = getText(s, text);
            if (s instanceof Player) {
                Player p = (Player)s;
            }
            s.sendMessage(text);
        }

        default void broadcast(String text) {
            if(text.isEmpty()) return;
            text = getText(null, text);
            Bukkit.getServer().broadcastMessage(text);
        }

        static String getText(CommandSender s, String text) {
            text = transform(text
                    .replace("%prefix%", zVipsBukkit.cfgu.prefix()));
            if(s instanceof Player) {
                Player p = (Player)s;
                text = transform(p, text);
            }
            return text;
        }
        static String getText(String text) {
            return getText(null, text);
        }

    }

}
