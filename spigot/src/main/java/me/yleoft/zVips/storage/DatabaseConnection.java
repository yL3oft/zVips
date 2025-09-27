package me.yleoft.zVips.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.yleoft.zAPI.folia.FoliaRunnable;
import me.yleoft.zAPI.utils.SchedulerUtils;
import me.yleoft.zVips.utils.ConfigUtils;
import me.yleoft.zVips.utils.LanguageUtils;
import me.yleoft.zVips.zVipsBukkit;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

import static me.yleoft.zAPI.utils.LocationUtils.serialize;

public class DatabaseConnection extends ConfigUtils {

    public static HikariDataSource dataSource = null;
    public static database_type type = database_type.SQLITE;

    public static final File libsFolder = new File(zVipsBukkit.getInstance().getDataFolder(), "libs");
    public static Driver mysqlDriver = null;
    public static Driver mariadbDriver = null;
    public static Driver h2Driver = null;

    public static final String mysqlVersion = "8.0.23";
    public static final String mysqlJar = "mysql-connector-java-" + mysqlVersion + ".jar";
    public static final String mysqlRepo = "https://repo1.maven.org/maven2/mysql/mysql-connector-java/" + mysqlVersion + "/" + mysqlJar;

    public static final String mariadbVersion = "3.5.3";
    public static final String mariadbJar = "mariadb-java-client-" + mariadbVersion + ".jar";
    public static final String mariadbRepo = "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/" + mariadbVersion + "/" + mariadbJar;

    public static final String h2Version = "2.3.232";
    public static final String h2Jar = "h2-" + h2Version + ".jar";
    public static final String h2Repo = "https://repo1.maven.org/maven2/com/h2database/h2/" + h2Version + "/" + h2Jar;

    boolean retry = false;

    public void connect() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                long start = System.currentTimeMillis();
                HikariConfig config = new HikariConfig();
                switch (databaseType().toLowerCase()) {
                    case "mariadb":
                        if (mariadbDriver == null) {
                            File mariadbjarFile = new File(libsFolder, mariadbJar);
                            URL mariadbjarURL = mariadbjarFile.toURI().toURL();
                            URLClassLoader mariadbclassLoader = new URLClassLoader(new URL[]{mariadbjarURL}, zVipsBukkit.class.getClassLoader());
                            Class<?> mariadbdriverClass = Class.forName("org.mariadb.jdbc.Driver", true, mariadbclassLoader);
                            mariadbDriver = (Driver) mariadbdriverClass.getDeclaredConstructor().newInstance();
                            DriverManager.registerDriver(new DriverShim(mariadbDriver));
                        }
                        type = database_type.EXTERNAL;
                        config.setJdbcUrl(mariadbUrl());
                        config.setUsername(databaseUsername());
                        config.setPassword(databasePassword());
                        break;
                    case "mysql":
                        if (mysqlDriver == null) {
                            try {
                                if (DriverManager.getDriver("jdbc:mysql://") != null) {
                                    DriverManager.deregisterDriver(DriverManager.getDriver("jdbc:mysql://"));
                                }
                            }catch (Exception ignored) {}
                            File mysqljarFile = new File(libsFolder, mysqlJar);
                            URL mysqljarURL = mysqljarFile.toURI().toURL();
                            URLClassLoader mysqlclassLoader = new URLClassLoader(new URL[]{mysqljarURL}, zVipsBukkit.class.getClassLoader());
                            Class<?> mysqldriverClass = Class.forName("com.mysql.cj.jdbc.Driver", true, mysqlclassLoader);
                            mysqlDriver = (Driver) mysqldriverClass.getDeclaredConstructor().newInstance();
                            DriverManager.registerDriver(new DriverShim(mysqlDriver));
                        }
                        type = database_type.EXTERNAL;
                        config.setJdbcUrl(mysqlUrl());
                        config.setUsername(databaseUsername());
                        config.setPassword(databasePassword());
                        break;
                    case "h2":
                        if (h2Driver == null) {
                            File h2jarFile = new File(libsFolder, h2Jar);
                            URL h2jarURL = h2jarFile.toURI().toURL();
                            URLClassLoader h2classLoader = new URLClassLoader(new URL[]{h2jarURL}, zVipsBukkit.class.getClassLoader());
                            Class<?> h2driverClass = Class.forName("org.h2.Driver", true, h2classLoader);
                            h2Driver = (Driver) h2driverClass.getDeclaredConstructor().newInstance();
                            DriverManager.registerDriver(new DriverShim(h2Driver));
                        }
                        type = database_type.H2;
                        config.setJdbcUrl(h2Url());
                        break;
                    default:
                        type = database_type.SQLITE;
                        config.setJdbcUrl(sqliteUrl());
                        break;
                }

                config.setMaximumPoolSize(databasePoolsize());
                dataSource = new HikariDataSource(config);
                long end = System.currentTimeMillis();
                zVipsBukkit.getInstance().getLogger().info("HikariCP startup took " + (end - start) + "ms");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error setting up HikariCP connection pool", e);
        }
    }

    public String mariadbUrl() {
        return "jdbc:mariadb://" + databaseHost() + ":" + databasePort() + "/" + databaseDatabase()+"?allowPublicKeyRetrieval="+databaseAllowPublicKeyRetrieval()+"&useSSL="+databaseUseSSL();
    }
    public String mysqlUrl() {
        return "jdbc:mysql://" + databaseHost() + ":" + databasePort() + "/" + databaseDatabase()+"?allowPublicKeyRetrieval="+databaseAllowPublicKeyRetrieval()+"&useSSL="+databaseUseSSL();
    }
    public String h2Url() {
        return "jdbc:h2:" + zVipsBukkit.getInstance().getDataFolder().getAbsolutePath() + "/database-h2";
    }
    public String sqliteUrl() {
        return "jdbc:sqlite:" + zVipsBukkit.getInstance().getDataFolder().getAbsolutePath() + "/database-sqlite.db";
    }

    public void disconnect() {
        if (dataSource != null) {
            closePool();
        }
    }

    public Connection getConnection() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            }
            disconnect();
            connect();
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to access database", e);
        }
    }

    public void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public void migrateData(@Nullable Player p, @NotNull String type) {
        SchedulerUtils.runTaskAsynchronously(new FoliaRunnable() {
        @Override
            public void run() {
            LanguageUtils.MainCMD.MainConverter lang = new LanguageUtils.MainCMD.MainConverter();
            switch(type) {
                case "sqlitetoh2": {
                    migrateLocalDatabase(p, sqliteUrl(), h2Url(),
                            "MERGE INTO " + databaseTable() + " (UUID, HOME, LOCATION) " +
                                    "KEY(UUID, HOME) VALUES (?, LEFT(?, 100), ?)"
                    );
                    break;
                }
                case "sqlitetomysql": {
                    migrateDatabase(p, sqliteUrl(), mysqlUrl(), "INSERT IGNORE INTO " + databaseTable() + " (UUID, HOME, LOCATION) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE LOCATION = VALUES(LOCATION)");
                    break;
                }
                case "sqlitetomariadb": {
                    migrateDatabase(p, sqliteUrl(), mariadbUrl(), "INSERT IGNORE INTO " + databaseTable() + " (UUID, HOME, LOCATION) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE LOCATION = VALUES(LOCATION)");
                    break;
                }
                case "mysqltosqlite": {
                    migrateDatabase2(p, mysqlUrl(), sqliteUrl(), "INSERT OR REPLACE INTO " + databaseTable() + " (UUID, HOME, LOCATION) VALUES (?, ?, ?)");
                    break;
                }
                case "mysqltoh2": {
                    migrateDatabase2(p, mysqlUrl(), h2Url(),
                            "MERGE INTO " + databaseTable() + " (UUID, HOME, LOCATION) " +
                                    "KEY(UUID, HOME) VALUES (?, LEFT(?, 100), ?)"
                    );
                    break;
                }
                case "mariadbtosqlite": {
                    migrateDatabase2(p, mariadbUrl(), sqliteUrl(), "INSERT OR REPLACE INTO " + databaseTable() + " (UUID, HOME, LOCATION) VALUES (?, ?, ?)");
                    break;
                }
                case "mariadbtoh2": {
                    migrateDatabase2(p, mariadbUrl(), h2Url(),
                            "MERGE INTO " + databaseTable() + " (UUID, HOME, LOCATION) " +
                                    "KEY(UUID, HOME) VALUES (?, LEFT(?, 100), ?)"
                    );
                    break;
                }
                case "h2tosqlite": {
                    migrateLocalDatabase(p, h2Url(), sqliteUrl(), "INSERT OR REPLACE INTO " + databaseTable() + " (UUID, HOME, LOCATION) VALUES (?, ?, ?)");
                    break;
                }
                case "h2tomysql": {
                    migrateDatabase(p, h2Url(), mysqlUrl(), "INSERT INTO " + databaseTable() + " (UUID, HOME, LOCATION) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE LOCATION = VALUES(LOCATION)");
                    break;
                }
                case "h2tomariadb": {
                    migrateDatabase(p, h2Url(), mariadbUrl(), "INSERT INTO " + databaseTable() + " (UUID, HOME, LOCATION) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE LOCATION = VALUES(LOCATION)");
                    break;
                }
                default: {
                    if (p != null) {
                        lang.sendMsg(p, lang.getUsage());
                    }
                }
            }
    }});
    }

    private void migrateDatabase(Player p, String sourceUrl, String targetUrl, String insertQuery) {
        disconnect();
        try (Connection sourceConn = DriverManager.getConnection(sourceUrl);
             Connection targetConn = DriverManager.getConnection(targetUrl, databaseUsername(), databasePassword())) {
            System.out.println("Connected to both source and target databases.");
            try (Statement stmt = sourceConn.createStatement();
                 ResultSet countResult = stmt.executeQuery("SELECT COUNT(*) AS total FROM " + databaseTable())) {
                countResult.next();
                int totalRows = countResult.getInt("total");
                if (totalRows == 0) {
                    System.out.println("No data to migrate.");
                    return;
                }
                System.out.println("Starting migration of " + totalRows + " records...");
                try (ResultSet resultSet = stmt.executeQuery("SELECT UUID, HOME, LOCATION FROM " + databaseTable())) {
                    try (PreparedStatement pstmt = targetConn.prepareStatement(insertQuery)) {
                        int count = 0;
                        while (resultSet.next()) {
                            pstmt.setString(1, resultSet.getString("UUID"));
                            pstmt.setString(2, resultSet.getString("HOME"));
                            pstmt.setString(3, resultSet.getString("LOCATION"));
                            pstmt.executeUpdate();
                            count++;
                            updateProgress(p, count, totalRows);
                        }
                        completeMigration(p, count, totalRows);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connect();
    }
    private void migrateDatabase2(Player p, String sourceUrl, String targetUrl, String insertQuery) {
        disconnect();
        try (Connection sourceConn = DriverManager.getConnection(sourceUrl, databaseUsername(), databasePassword());
             Connection targetConn = DriverManager.getConnection(targetUrl)) {
            System.out.println("Connected to both source and target databases.");
            try (Statement stmt = sourceConn.createStatement();
                 ResultSet countResult = stmt.executeQuery("SELECT COUNT(*) AS total FROM " + databaseTable())) {
                countResult.next();
                int totalRows = countResult.getInt("total");
                if (totalRows == 0) {
                    System.out.println("No data to migrate.");
                    return;
                }
                System.out.println("Starting migration of " + totalRows + " records...");
                try (ResultSet resultSet = stmt.executeQuery("SELECT UUID, HOME, LOCATION FROM " + databaseTable())) {
                    try (PreparedStatement pstmt = targetConn.prepareStatement(insertQuery)) {
                        int count = 0;
                        while (resultSet.next()) {
                            pstmt.setString(1, resultSet.getString("UUID"));
                            pstmt.setString(2, resultSet.getString("HOME"));
                            pstmt.setString(3, resultSet.getString("LOCATION"));
                            pstmt.executeUpdate();
                            count++;
                            updateProgress(p, count, totalRows);
                        }
                        completeMigration(p, count, totalRows);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connect();
    }
    private void migrateLocalDatabase(Player p, String sourceUrl, String targetUrl, String insertQuery) {
        disconnect();
        try (Connection sourceConn = DriverManager.getConnection(sourceUrl);
             Connection targetConn = DriverManager.getConnection(targetUrl)) {
            System.out.println("Connected to both source and target databases.");
            try (Statement stmt = sourceConn.createStatement();
                 ResultSet countResult = stmt.executeQuery("SELECT COUNT(*) AS total FROM " + databaseTable())) {
                countResult.next();
                int totalRows = countResult.getInt("total");
                if (totalRows == 0) {
                    System.out.println("No data to migrate.");
                    return;
                }
                System.out.println("Starting migration of " + totalRows + " records...");
                try (ResultSet resultSet = stmt.executeQuery("SELECT UUID, HOME, LOCATION FROM " + databaseTable())) {
                    try (PreparedStatement pstmt = targetConn.prepareStatement(insertQuery)) {
                        int count = 0;
                        while (resultSet.next()) {
                            pstmt.setString(1, resultSet.getString("UUID"));
                            pstmt.setString(2, resultSet.getString("HOME"));
                            pstmt.setString(3, resultSet.getString("LOCATION"));
                            pstmt.executeUpdate();
                            count++;
                            updateProgress(p, count, totalRows);
                        }
                        completeMigration(p, count, totalRows);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connect();
    }
    private void updateProgress(Player p, int count, int totalRows) {
        if (p != null) {
            String message = ChatColor.translateAlternateColorCodes('&', "&aConverting Data... &8[&7" + count + "/" + totalRows + "&8]");
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }
    private void completeMigration(Player p, int count, int totalRows) {
        if (p != null) {
            String message = ChatColor.translateAlternateColorCodes('&', "&aConverted Data! &8[&7" + count + "/" + totalRows + "&8]");
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100.0F, 1.0F);
        }
        System.out.println("\nMigration completed! " + count + " records transferred.");
    }

    public boolean existsTableColumnValue(String table, String columnName, String value) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + table + " WHERE " + columnName + "=?")) {
            ps.setString(1, value);
            try (ResultSet results = ps.executeQuery()) {
                if (results.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsTableColumnValue(String table, String columnName, int value) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + table + " WHERE " + columnName + "=?")) {
            ps.setInt(1, value);
            try (ResultSet results = ps.executeQuery()) {
                if (results.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsTableColumnValueDouble(String table, String columnName, String value, String columnName2, String value2) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + table + " WHERE " + columnName + "=? AND " + columnName2 + "=?")) {
            ps.setString(1, value);
            ps.setString(2, value2);
            try (ResultSet results = ps.executeQuery()) {
                if (results.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsTableColumnValueDouble(String table, String columnName, String value, String columnName2, int value2) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM " + table + " WHERE " + columnName + "=? AND " + columnName2 + "=?")) {
            ps.setString(1, value);
            ps.setInt(2, value2);
            try (ResultSet results = ps.executeQuery()) {
                if (results.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class DriverShim implements Driver {
        private final Driver driver;

        public DriverShim(Driver driver) {
            this.driver = driver;
        }

        @Override
        public boolean acceptsURL(String u) throws SQLException {
            return driver.acceptsURL(u);
        }

        @Override
        public Connection connect(String u, Properties p) throws SQLException {
            return driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return driver.getParentLogger();
        }
    }

}