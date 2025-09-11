package me.yleoft.zVips.utils;

import me.yleoft.zAPI.utils.StringUtils;
import me.yleoft.zVips.zVips;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ConfigUtils {
    private static final zVips main = zVips.getInstance();

    protected String cmdPath = "commands.";
    protected String permissionsPath = "permissions.";
    protected String permissionsBypassPath = permissionsPath+"bypass.";
    protected String databasePath = "database.";
    protected String vipo = "vip-options.";
    protected String vipocooldown = vipo+"cooldowns.";
    protected String partyVip = "party-vip.";
    protected String partyVipActivation = partyVip+"activation.";

    public ConfigUtilsExtras cfguExtras = new ConfigUtilsExtras();

    public static String langType() {
        return main.getConfig().getString("general.language");
    }
    public Boolean hasMetrics() {
        return main.getConfig().getBoolean("general.metrics");
    }
    public String prefix() {
        return StringUtils.transform(requireNonNull(main.getConfig().getString("prefix")));
    }
    public Boolean enableLogs() {
        return main.getConfig().getBoolean("general.enable-logs");
    }

    //<editor-fold desc="Database">
    public String databaseType() {
        return main.getConfig().getString(this.databasePath + "type");
    }
    public String databaseHost() {
        return main.getConfig().getString(this.databasePath + "host");
    }
    public Integer databasePort() {
        return main.getConfig().getInt(this.databasePath + "port");
    }
    public String databaseDatabase() {
        return main.getConfig().getString(this.databasePath + "database");
    }
    public String databaseUsername() {
        return main.getConfig().getString(this.databasePath + "username");
    }
    public String databasePassword() {
        return main.getConfig().getString(this.databasePath + "password");
    }
    public Boolean databaseUseSSL() {
        return main.getConfig().getBoolean(this.databasePath + "options.useSSL");
    }
    public Boolean databaseAllowPublicKeyRetrieval() {
        return main.getConfig().getBoolean(this.databasePath + "options.allowPublicKeyRetrieval");
    }
    public int databasePoolsize() {
        return main.getConfig().getInt(this.databasePath + "pool-size");
    }
    public String databaseTablePrefix() {
        return requireNonNull(main.getConfig().getString(this.databasePath + "table-prefix")).toLowerCase();
    }
    public String databaseTable() {
        return databaseTablePrefix()+"_keys";
    }
    public String databaseTable2() {
        return databaseTablePrefix()+"_players";
    }
    public String databaseTable3() {
        return databaseTablePrefix()+"_vips";
    }
    public String databaseTable4() {
        return databaseTablePrefix()+"_settings";
    }
    //</editor-fold>

    //<editor-fold desc="Plugin Information">
    public boolean requireConfirmationForKey() {
        return main.getConfig().getBoolean(vipo+"require-confirmation-for-key");
    }
    public boolean requireEmptyInventory() {
        return main.getConfig().getBoolean(vipo+"require-empty-inventory");
    }
    public boolean decreaseInactiveVips() {
        return main.getConfig().getBoolean(vipo+"decrease-inactive-vips");
    }
    public int changevipCooldown() {
        return main.getConfig().getInt(vipocooldown+"changevip") >= 0 ? main.getConfig().getInt(vipocooldown+"changevip") : 600;
    }
    public int transferkeyCooldown() {
        return main.getConfig().getInt(vipocooldown+"transferkey") >= 0 ? main.getConfig().getInt(vipocooldown+"transferkey") : 600;
    }
    //</editor-fold>

    //<editor-fold desc="Party Vip">
    public int pvipObjective() {
        return main.getConfig().getInt(partyVip+"objective") > 0 ? main.getConfig().getInt(partyVip+"objective") : 250;
    }
    public List<String> pvipCommands() {
        String path = partyVipActivation+"commands";
        return main.getConfig().isList(path) ? main.getConfig().getStringList(path) : Collections.singletonList(main.getConfig().getString(path));
    }
    //</editor-fold>

    //<editor-fold desc="Main Command">
    public String CmdMainCommand() {
        return main.getConfig().getString(this.cmdPath + "main.command");
    }
    public String CmdMainPermission() {
        return main.getConfig().getString(this.cmdPath + "main.permission");
    }
    public String CmdMainDescription() {
        return main.getConfig().getString(this.cmdPath + "main.description");
    }
    public Double CmdMainCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "main.cooldown");
    }
    public List<String> CmdMainAliases() {
        return main.getConfig().getStringList(this.cmdPath + "main.aliases");
    }
    public String CmdMainHelpPermission() {
        return main.getConfig().getString(this.cmdPath + "main.help.permission");
    }
    public String CmdMainVersionPermission() {
        return main.getConfig().getString(this.cmdPath + "main.version.permission");
    }
    public String CmdMainReloadPermission() {
        return main.getConfig().getString(this.cmdPath + "main.reload.permission");
    }
    public String CmdMainConverterPermission() {
        return main.getConfig().getString(this.cmdPath + "main.converter.permission");
    }
    //</editor-fold
    //<editor-fold desc="Pointsvip Command">
    public String CmdPointsvipCommand() {
        return main.getConfig().getString(this.cmdPath + "pointsvip.command");
    }
    public String CmdPointsvipPermission() {
        return main.getConfig().getString(this.cmdPath + "pointsvip.permission");
    }
    public String CmdPointsvipDescription() {
        return main.getConfig().getString(this.cmdPath + "pointsvip.description");
    }
    public Double CmdPointsvipCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "pointsvip.cooldown");
    }
    public List<String> CmdPointsvipAliases() {
        return main.getConfig().getStringList(this.cmdPath + "pointsvip.aliases");
    }
    public String CmdPointsvipOthersPermission() {
        return main.getConfig().getString(this.cmdPath + "pointsvip.others.permission");
    }
    public String CmdPointsvipManagePermission() {
        return main.getConfig().getString(this.cmdPath + "pointsvip.manage.permission");
    }
    //</editor-fold
    //<editor-fold desc="Partyvip Command">
    public String CmdPartyvipCommand() {
        return main.getConfig().getString(this.cmdPath + "partyvip.command");
    }
    public String CmdPartyvipPermission() {
        return main.getConfig().getString(this.cmdPath + "partyvip.permission");
    }
    public String CmdPartyvipDescription() {
        return main.getConfig().getString(this.cmdPath + "partyvip.description");
    }
    public Double CmdPartyvipCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "partyvip.cooldown");
    }
    public List<String> CmdPartyvipAliases() {
        return main.getConfig().getStringList(this.cmdPath + "partyvip.aliases");
    }
    public String CmdPartyvipManagePermission() {
        return main.getConfig().getString(this.cmdPath + "partyvip.manage.permission");
    }
    //</editor-fold>
    //<editor-fold desc="Givekey Command">
    public String CmdGivekeyCommand() {
        return main.getConfig().getString(this.cmdPath + "givekey.command");
    }
    public String CmdGivekeyPermission() {
        return main.getConfig().getString(this.cmdPath + "givekey.permission");
    }
    public String CmdGivekeyDescription() {
        return main.getConfig().getString(this.cmdPath + "givekey.description");
    }
    public Double CmdGivekeyCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "givekey.cooldown");
    }
    public List<String> CmdGivekeyAliases() {
        return main.getConfig().getStringList(this.cmdPath + "givekey.aliases");
    }
    //</editor-fold>
    //<editor-fold desc="Genkey Command">
    public String CmdGenkeyCommand() {
        return main.getConfig().getString(this.cmdPath + "genkey.command");
    }
    public String CmdGenkeyPermission() {
        return main.getConfig().getString(this.cmdPath + "genkey.permission");
    }
    public String CmdGenkeyDescription() {
        return main.getConfig().getString(this.cmdPath + "genkey.description");
    }
    public Double CmdGenkeyCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "genkey.cooldown");
    }
    public List<String> CmdGenkeyAliases() {
        return main.getConfig().getStringList(this.cmdPath + "genkey.aliases");
    }
    //</editor-fold>
    //<editor-fold desc="Listkeys Command">
    public String CmdListkeysCommand() {
        return main.getConfig().getString(this.cmdPath + "listkeys.command");
    }
    public String CmdListkeysPermission() {
        return main.getConfig().getString(this.cmdPath + "listkeys.permission");
    }
    public String CmdListkeysDescription() {
        return main.getConfig().getString(this.cmdPath + "listkeys.description");
    }
    public Double CmdListkeysCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "listkeys.cooldown");
    }
    public List<String> CmdListkeysAliases() {
        return main.getConfig().getStringList(this.cmdPath + "listkeys.aliases");
    }
    public String CmdListkeysOthersPermission() {
        return main.getConfig().getString(this.cmdPath + "listkeys.others.permission");
    }
    //</editor-fold>
    //<editor-fold desc="Usekey Command">
    public String CmdUsekeyCommand() {
        return main.getConfig().getString(this.cmdPath + "usekey.command");
    }
    public String CmdUsekeyPermission() {
        return main.getConfig().getString(this.cmdPath + "usekey.permission");
    }
    public String CmdUsekeyDescription() {
        return main.getConfig().getString(this.cmdPath + "usekey.description");
    }
    public Double CmdUsekeyCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "usekey.cooldown");
    }
    public List<String> CmdUsekeyAliases() {
        return main.getConfig().getStringList(this.cmdPath + "usekey.aliases");
    }
    public String CmdUsekeyOthersPermission() {
        return main.getConfig().getString(this.cmdPath + "usekey.others.permission");
    }
    //</editor-fold>
    //<editor-fold desc="Transferkey Command">
    public String CmdTransferkeyCommand() {
        return main.getConfig().getString(this.cmdPath + "transferkey.command");
    }
    public String CmdTransferkeyPermission() {
        return main.getConfig().getString(this.cmdPath + "transferkey.permission");
    }
    public String CmdTransferkeyDescription() {
        return main.getConfig().getString(this.cmdPath + "transferkey.description");
    }
    public Double CmdTransferkeyCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "transferkey.cooldown");
    }
    public List<String> CmdTransferkeyAliases() {
        return main.getConfig().getStringList(this.cmdPath + "transferkey.aliases");
    }
    //</editor-fold>
    //<editor-fold desc="Deletekey Command">
    public String CmdDeletekeyCommand() {
        return main.getConfig().getString(this.cmdPath + "deletekey.command");
    }
    public String CmdDeletekeyPermission() {
        return main.getConfig().getString(this.cmdPath + "deletekey.permission");
    }
    public String CmdDeletekeyDescription() {
        return main.getConfig().getString(this.cmdPath + "deletekey.description");
    }
    public Double CmdDeletekeyCooldown() {
        return main.getConfig().getDouble(this.cmdPath + "deletekey.cooldown");
    }
    public List<String> CmdDeletekeyAliases() {
        return main.getConfig().getStringList(this.cmdPath + "deletekey.aliases");
    }
    //</editor-fold>

    //<editor-fold desc="Permissions">
    public String PermissionBypassChangevip() {
        return main.getConfig().getString(permissionsBypassPath+"changevip");
    }
    public String PermissionBypassTransferkey() {
        return main.getConfig().getString(permissionsBypassPath+"transferkey");
    }
    public String PermissionBypassCommandCost(String commandPermission) {
        return requireNonNull(main.getConfig().getString(permissionsBypassPath + "command-cost"))
                .replace("%command_permission%", commandPermission);
    }
    //</editor-fold>

    public static class ConfigUtilsExtras {

        public boolean canAfford(Player p, String commandPermission, Float cost) {
            if(p.hasPermission(zVips.cfgu.PermissionBypassCommandCost(commandPermission))) {
                return true;
            }
            Economy economy = (Economy) zVips.economy;
            LanguageUtils.HooksMSG hooks = new LanguageUtils.HooksMSG();
            if (zVips.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {
                if(economy.has(p, cost)) {
                    economy.withdrawPlayer(p, cost);
                    return true;
                }
                hooks.sendMsg(p, hooks.getVaultCantAfford(cost));
                return false;
            }
            return true;
        }

    }

}
