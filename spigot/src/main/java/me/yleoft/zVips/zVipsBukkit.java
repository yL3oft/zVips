package me.yleoft.zVips;

import me.yleoft.zAPI.Metrics;
import me.yleoft.zAPI.managers.FileManager;
import me.yleoft.zAPI.managers.LanguageManager;
import me.yleoft.zAPI.utils.FileUtils;
import me.yleoft.zAPI.zAPI;
import me.yleoft.zVips.commands.*;
import me.yleoft.zVips.completers.*;
import me.yleoft.zVips.hooks.*;
import me.yleoft.zVips.storage.*;
import me.yleoft.zVips.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;
import static me.yleoft.zAPI.managers.PluginYAMLManager.*;
import static me.yleoft.zAPI.utils.StringUtils.transform;
import static me.yleoft.zVips.storage.DatabaseConnection.*;
import static me.yleoft.zVips.utils.LanguageUtils.loadzAPIMessages;

public final class zVipsBukkit extends JavaPlugin {

    public static FileUtils configFileUtils;
    public static boolean usePlaceholderAPI = false;
    public static boolean useVault = false;

    private static zVipsBukkit main;
    public static ConfigUtils cfgu;
    public static LanguageManager langm;
    public static DatabaseConnection db;
    public static DatabaseEditor dbe;
    public static zVipsBukkit getInstance() {
        return main;
    }

    public static String transfers = "transfers";
    public static String keygens = "keygens";
    public static String activations = "activations";

    public static Object papi;
    public static Object economy;

    public final String pluginName = getDescription().getName();
    public String coloredPluginName = getDescription().getName();
    public final String pluginVer = getDescription().getVersion();
    public static int bStatsId = 26917;

    @Override
    public void onEnable() {
        zAPI.init(this, getDescription().getName(), coloredPluginName, false);
        main = this;
        cfgu = new ConfigUtils();
        db = new DatabaseConnection();
        dbe = new DatabaseEditor();
        coloredPluginName = transform(requireNonNull(getConfig().getString("prefix")));
        zAPI.setColoredPluginName(coloredPluginName);
        //<editor-fold desc="Files">
        LanguageUtils.Helper helper = new LanguageUtils.Helper() {};
        coloredPluginName = transform(requireNonNull(cfgu.prefix()));
        helper.sendMsg(getServer().getConsoleSender(), ChatColor.translateAlternateColorCodes('&', coloredPluginName + "&fChecking if files exist..."));
        if(configFileUtils == null) {
            getConfig();
        }
        List<FileUtils> fus = new ArrayList<>();
        fus.add(FileManager.createFile("languages/en.yml"));
        fus.add(FileManager.createFile("languages/pt-br.yml"));
        for(FileUtils fu : fus) {
            fu.saveDefaultConfig();
            fu.reloadConfig();
        }
        try {
            langm = new LanguageManager(new File(getDataFolder(), "languages"), langType(), "en");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load language file", e);
        }
        File dirVips = new File(getDataFolder(), "vips");
        if(dirVips.mkdir()) {
            List<FileUtils> fusVips = new ArrayList<>();
            fusVips.add(new FileUtils(new File(getDataFolder(), "vips/premium.yml"), "vips/premium.yml"));
            fusVips.add(new FileUtils(new File(getDataFolder(), "vips/elite.yml"), "vips/elite.yml"));
            for(FileUtils fu : fusVips) {
                fu.saveDefaultConfig();
                fu.reloadConfig();
            }
        }
        helper.sendMsg(getServer().getConsoleSender(), ChatColor.translateAlternateColorCodes('&', coloredPluginName + "&fAll files have been created!"));
        //</editor-fold>
        //<editor-fold desc="Download Libs">
        if(!libsFolder.exists()) {
            try {
                Files.createDirectories(Paths.get(libsFolder.toURI()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //<editor-fold desc="H2">
        try {
            File outputFile = new File(libsFolder, h2Jar);
            if(!outputFile.exists()) {
                downloadFile(h2Repo, outputFile);
                helper.sendMsg(getServer().getConsoleSender(), coloredPluginName+"§aLibrary §9H2Database §asaved to "+outputFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to download H2Database library", e);
        }
        //</editor-fold>
        //<editor-fold desc="MySQL">
        try {
            File outputFile = new File(libsFolder, mysqlJar);
            if(!outputFile.exists()) {
                downloadFile(mysqlRepo, outputFile);
                helper.sendMsg(getServer().getConsoleSender(), coloredPluginName+"§aLibrary §9MySQL Connector Java §asaved to "+outputFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to download MySQL Connector Java library", e);
        }
        //</editor-fold>
        //<editor-fold desc="MariaDB">
        try {
            File outputFile = new File(libsFolder, mariadbJar);
            if(!outputFile.exists()) {
                downloadFile(mariadbRepo, outputFile);
                helper.sendMsg(getServer().getConsoleSender(), coloredPluginName+"§aLibrary §9MariaDB Java Client §asaved to "+outputFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to download MariaDB library", e);
        }
        //</editor-fold>
        //</editor-fold>
        //<editor-fold desc="Database">
        db.connect();
        dbe.createTable(
                db.databaseTable(),
                "(GKEY VARCHAR(12) NOT NULL, ID INT NOT NULL DEFAULT 1 CHECK (ID > 0) UNIQUE, VIP VARCHAR(30) NOT NULL, DURATION BIGINT NOT NULL DEFAULT 0 CHECK (DURATION >= 0), USES INT NOT NULL DEFAULT 1 CHECK (USES > 0), PLAYER VARCHAR(36), PRIMARY KEY (GKEY))"
        );
        dbe.createTable(
                db.databaseTable2(),
                "(UUID VARCHAR(36) NOT NULL, POINTS BIGINT NOT NULL DEFAULT 0 CHECK (POINTS >= 0), ACTIVATED SMALLINT NOT NULL DEFAULT 0 CHECK (ACTIVATED >= 0), ACTIVE VARCHAR(30), PRIMARY KEY (UUID))"
        );
        dbe.createTable(
                db.databaseTable3(),
                "(ID INT AUTO_INCREMENT, UUID VARCHAR(36) NOT NULL, VIP VARCHAR(30), EXPIRE BIGINT NOT NULL DEFAULT 0 CHECK (EXPIRE >= 0), LASTUPDATE BIGINT NOT NULL DEFAULT 0 CHECK (DURATION >= 0), PRIMARY KEY (ID))"
        );
        dbe.createTable(
                db.databaseTable4(),
                "(SETTING VARCHAR(255) NOT NULL, SVALUE VARCHAR(255), IVALUE INT, PRIMARY KEY (SETTING))"
        );
        //</editor-fold>
        //<editor-fold desc="Metrics">
        if(cfgu.hasMetrics()) {
            Metrics metrics = zAPI.startMetrics(bStatsId);
            metrics.addCustomChart(new Metrics.DrilldownPie("player_count", () -> {
                int players = Bukkit.getOnlinePlayers().size();
                return buildDistribution(players);
            }));
            helper.sendMsg(getServer().getConsoleSender(), ChatColor.translateAlternateColorCodes('&',
                    coloredPluginName+"&aMetrics enabled."
            ));
        }
        //</editor-fold>
        loadCommands();
        loadzAPIMessages();
        //<editor-fold desc="Hooks">
        helper.sendMsg(getServer().getConsoleSender(), coloredPluginName + "§fTrying to connect to hooks...");
        //<editor-fold desc="PlaceholderAPI">
        try {
            zAPI.setPlaceholderAPIHandler(new PlaceholderAPIHandler());
            if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                usePlaceholderAPI = true;
                zAPI.registerPlaceholderExpansion(getDescription().getAuthors().toString(), pluginVer, true, true);
                papi = zAPI.getPlaceholderExpansion();
                helper.sendMsg(getServer().getConsoleSender(), coloredPluginName + "§aPlaceholderAPI hooked successfully!");
            } else {
                helper.sendMsg(getServer().getConsoleSender(), coloredPluginName + "§cPlaceholderAPI plugin not found! Disabling hook...");
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error hooking into PlaceholderAPI", e);
        }
        //</editor-fold>
        //<editor-fold desc="Vault">
        try {
            if (getServer().getPluginManager().isPluginEnabled("Vault")) {
                useVault = true;
                economy = zAPI.setupEconomy();
                helper.sendMsg(getServer().getConsoleSender(), coloredPluginName + "§aConnected to Vault successfully!");
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error hooking into VaultAPI", e);
        }
        //</editor-fold>
        //</editor-fold>
    }

    @Override
    public void onDisable() {
        zAPI.disable();
        if (db != null) {
            db.closePool();
            getLogger().info("Database connection closed.");
        }
        main = null;
    }

    public void loadCommands() {
        LanguageUtils.CommandsMSG helper = new LanguageUtils.CommandsMSG();
        helper.sendMsg(getServer().getConsoleSender(), ChatColor.translateAlternateColorCodes('&', coloredPluginName + "§fTrying to load commands, permissions & events..."));
        //<editor-fold desc="Commands">
        try {
            unregisterCommands();
            registerCommand(cfgu.CmdMainCommand(), new MainCommand(), cfgu.CmdMainCooldown(), new MainCompleter(), cfgu.CmdMainDescription(), cfgu.CmdMainAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdPointsvipCommand(), new PointsvipCommand(), cfgu.CmdPointsvipCooldown(), new PointsvipCompleter(), cfgu.CmdPointsvipDescription(), cfgu.CmdPointsvipAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdPartyvipCommand(), new PartyvipCommand(), cfgu.CmdPartyvipCooldown(), new PartyvipCompleter(), cfgu.CmdPartyvipDescription(), cfgu.CmdPartyvipAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdGivekeyCommand(), new GivekeyCommand(), cfgu.CmdGivekeyCooldown(), new GivekeyCompleter(), cfgu.CmdGivekeyDescription(), cfgu.CmdGivekeyAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdGenkeyCommand(), new GenkeyCommand(), cfgu.CmdGenkeyCooldown(), new GenkeyCompleter(), cfgu.CmdGenkeyDescription(), cfgu.CmdGenkeyAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdListkeysCommand(), new ListkeysCommand(), cfgu.CmdListkeysCooldown(), new ListkeysCompleter(), cfgu.CmdListkeysDescription(), cfgu.CmdListkeysAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdUsekeyCommand(), new UsekeyCommand(), cfgu.CmdUsekeyCooldown(), new UsekeyCompleter(), cfgu.CmdUsekeyDescription(), cfgu.CmdUsekeyAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdTransferkeyCommand(), new TransferkeyCommand(), cfgu.CmdTransferkeyCooldown(), new TransferkeyCompleter(), cfgu.CmdTransferkeyDescription(), cfgu.CmdTransferkeyAliases().toArray(new String[0]));
            registerCommand(cfgu.CmdDeletekeyCommand(), new DeletekeyCommand(), cfgu.CmdDeletekeyCooldown(), cfgu.CmdDeletekeyDescription(), cfgu.CmdDeletekeyAliases().toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load commands", e);
        }
        //</editor-fold>
        //<editor-fold desc="Permissions">
        try {
            unregisterPermissions();
            Map<String, Boolean> helpANDmainChildren = new HashMap<>();
            helpANDmainChildren.put(cfgu.CmdMainPermission(), true);
            helpANDmainChildren.put(cfgu.CmdMainHelpPermission(), true);
            registerPermission(cfgu.CmdMainPermission(), "Permission to use the '/" + cfgu.CmdMainCommand() + "' command", PermissionDefault.TRUE);
            registerPermission(cfgu.CmdMainHelpPermission(), "Permission to use the '/" + cfgu.CmdMainCommand() + " (help|?)' command (With perm)", PermissionDefault.OP);
            registerPermission(cfgu.CmdMainVersionPermission(), "Permission to use the '/" + cfgu.CmdMainCommand() + " (version|ver)' command", PermissionDefault.TRUE);
            registerPermission(cfgu.CmdMainReloadPermission(), "Permission to use the '/" + cfgu.CmdMainCommand() + " (reload|rl)' command", PermissionDefault.OP, helpANDmainChildren);
            registerPermission(cfgu.CmdMainConverterPermission(), "Permission to use the '/" + cfgu.CmdMainCommand() + " (converter) [type]' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdPointsvipPermission(), "Permission to use the '/" + cfgu.CmdPointsvipCommand() + "' command", PermissionDefault.TRUE);
            registerPermission(cfgu.CmdPointsvipOthersPermission(), "Permission to use the '/" + cfgu.CmdPointsvipCommand() + " (player)' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdPointsvipManagePermission(), "Permission to use the '/" + cfgu.CmdPointsvipCommand() + " (set|add|remove|reset) (player)' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdPartyvipPermission(), "Permission to use the '/" + cfgu.CmdPartyvipCommand() + "' command", PermissionDefault.TRUE);
            registerPermission(cfgu.CmdPartyvipManagePermission(), "Permission to use the '/" + cfgu.CmdPartyvipCommand() + " (set|add|remove|reset)' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdGivekeyPermission(), "Permission to use the '/" + cfgu.CmdGivekeyCommand() + "' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdGenkeyPermission(), "Permission to use the '/" + cfgu.CmdGenkeyCommand() + "' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdListkeysPermission(), "Permission to use the '/" + cfgu.CmdListkeysCommand() + "' command", PermissionDefault.TRUE);
            registerPermission(cfgu.CmdListkeysOthersPermission(), "Permission to use the '/" + cfgu.CmdListkeysCommand() + " [Player]' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdUsekeyPermission(), "Permission to use the '/" + cfgu.CmdUsekeyCommand() + "' command", PermissionDefault.TRUE);
            registerPermission(cfgu.CmdUsekeyOthersPermission(), "Permission to use the '/" + cfgu.CmdUsekeyCommand() + " (Key) [Player]' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdTransferkeyPermission(), "Permission to use the '/" + cfgu.CmdTransferkeyCommand() + "' command", PermissionDefault.OP);
            registerPermission(cfgu.CmdDeletekeyPermission(), "Permission to use the '/" + cfgu.CmdDeletekeyPermission() + "' command", PermissionDefault.OP);
            registerPermission(cfgu.PermissionBypassChangevip(), "Permission to bypass changevip cooldowns", PermissionDefault.OP);
            registerPermission(cfgu.PermissionBypassTransferkey(), "Permission to bypass transferkey cooldowns", PermissionDefault.OP);
        } catch (Exception e) {
            e.printStackTrace();
            helper.sendMsg(getServer().getConsoleSender(), this.coloredPluginName + "§cError registering permissions (This doesn't affect anything in general)!");
        }
        //</editor-fold>
        //<editor-fold desc="Listeners">
        //</editor-fold>
    }
    public static void downloadFile(String fileURL, File saveFilePath) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        } else {
            System.out.println("Error downloading libs. HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    //<editor-fold desc="bStats">
    private Map<String, Map<String, Integer>> buildDistribution(int players) {
        Map<String, Map<String, Integer>> outer = new HashMap<>();
        String outerBucket = outerBucket(players);
        String innerBucket = innerBucket(players);
        Map<String, Integer> inner = new HashMap<>();
        inner.put(innerBucket, 1);
        outer.put(outerBucket, inner);
        return outer;
    }

    private String outerBucket(int n) {
        if (n <= 0) return "0";
        if (n <= 10) return "1-10";
        if (n <= 25) return "11-25";
        if (n <= 50) return "26-50";
        if (n <= 100) return "51-100";
        if (n <= 200) return "101-200";
        return "200+";
    }

    private String innerBucket(int n) {
        if (n <= 0) return "0";
        if (n <= 10) {
            return String.valueOf(n);
        }
        if (n <= 25) {
            return rangeOfFive(n, 11, 25);
        }
        if (n <= 50) {
            return rangeOfFive(n, 26, 50);
        }
        if (n <= 100) {
            return rangeOfFive(n, 51, 100);
        }
        if (n <= 200) {
            return rangeOfFive(n, 101, 200);
        }
        return "200+";
    }

    private String rangeOfFive(int n, int start, int end) {
        int bucketStart = ((n - start) / 5) * 5 + start;
        int bucketEnd = Math.min(bucketStart + 4, end);
        return bucketStart + "-" + bucketEnd;
    }
    //</editor-fold>
    //<editor-fold desc="Java Overrides">
    @Override
    public @NotNull FileConfiguration getConfig() {
        if (configFileUtils == null) {
            // Lazy initialization if not already set
            File configFile = new File(getDataFolder(), "config.yml");
            configFileUtils = new FileUtils(configFile, "config.yml");
            configFileUtils.saveDefaultConfig();
            configFileUtils.reloadConfig(false);
        }
        return configFileUtils.getConfig();
    }

    @Override
    public void saveDefaultConfig() {
        if (configFileUtils == null) {
            File configFile = new File(getDataFolder(), "config.yml");
            configFileUtils = new FileUtils(configFile, "config.yml");
        }
        configFileUtils.saveDefaultConfig();
    }

    @Override
    public void reloadConfig() {
        if (configFileUtils == null) {
            File configFile = new File(getDataFolder(), "config.yml");
            configFileUtils = new FileUtils(configFile, "config.yml");
            configFileUtils.saveDefaultConfig();
        }
        configFileUtils.reloadConfig(false);
    }
    //</editor-fold>
}
