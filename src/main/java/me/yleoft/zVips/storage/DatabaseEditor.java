package me.yleoft.zVips.storage;

import me.yleoft.zAPI.managers.LogManager;
import me.yleoft.zAPI.utils.LogUtils;
import me.yleoft.zAPI.utils.PlayerUtils;
import me.yleoft.zVips.constructors.KEY;
import me.yleoft.zVips.constructors.VIP;
import me.yleoft.zVips.utils.PartyvipUtils;
import me.yleoft.zVips.zVips;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseEditor extends DatabaseConnection {

    public void createTable(String table, String coluns) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + coluns)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error creating table: " + table, e);
        }
    }

    //<editor-fold desc="Keys">
    public int setKey(String key, String vip, long duration, int uses, @Nullable String uuid) {
        try (Connection con = getConnection()) {
            int id = increaseCurrentKeyID();
            if (isInTable(databaseTable(), "GKEY", key)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable() + " SET ID=?, VIP=?, DURATION=?, USES=?, PLAYER=? WHERE KEY=?")) {
                    ps.setInt(1, id);
                    ps.setString(2, vip);
                    ps.setLong(3, duration);
                    ps.setInt(4, uses);
                    if (uuid == null) {
                        ps.setNull(5, Types.VARCHAR);
                    } else {
                        ps.setString(5, uuid);
                    }
                    ps.setString(6, key);
                    ps.executeUpdate();
                }
            } else {
                String query;
                if (type == database_type.H2) {
                    query = "MERGE INTO " + databaseTable() + " KEY(GKEY) VALUES (?, ?, ?, ?, ?, ?)";
                } else {
                    query = "INSERT INTO " + databaseTable() + " (GKEY, ID, VIP, DURATION, USES, PLAYER) VALUES (?, ?, ?, ?, ?, ?)";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, key);
                    ps.setInt(2, id);
                    ps.setString(3, vip);
                    ps.setLong(4, duration);
                    ps.setInt(5, uses);
                    if (uuid == null) {
                        ps.setNull(6, Types.VARCHAR);
                    } else {
                        ps.setString(6, uuid);
                    }
                    ps.executeUpdate();
                }
            }
            return id;
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error setting key: " + key, e);
        }
        return getCurrentKeyID();
    }
    public int setKey(KEY k) {
        return setKey(k.getKey(), k.getVipName(), k.getDuration(), k.getUses(), (k.getOwner() == null ? null : k.getOwner().getUniqueId().toString()));
    }
    public int setKey(String key, String vip, long duration, int uses, @Nullable OfflinePlayer p) {
        return setKey(key, vip, duration, uses, (p == null ? null : p.getUniqueId().toString()));
    }

    public int decreaseUses(KEY key) {
        int newUses = key.getUses()-1;
        int id = key.getID();
        if(newUses <= 0) {
            deleteKey(id);
        }
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable(), "ID", id)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable() + " SET USES=USES-1 WHERE ID=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error decreasing key uses for key: " + key, e);
        }
        return newUses;
    }

    public KEY getKey(int id) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable(), "ID", id)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT GKEY, VIP, DURATION, USES, PLAYER from " + databaseTable() + " WHERE ID=?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String gkey = rs.getString("GKEY");
                            String vip = rs.getString("VIP");
                            long duration = rs.getLong("DURATION");
                            String uuid = rs.getString("PLAYER");
                            int uses = rs.getInt("USES");
                            OfflinePlayer p = (uuid == null ? null : PlayerUtils.getOfflinePlayer(UUID.fromString(uuid)));
                            return new KEY(gkey, id, vip, duration, uses, p);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error retrieving key with ID: " + id, e);
        }
        return null;
    }
    public KEY getKey(String key) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable(), "GKEY", key)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT ID, VIP, DURATION, USES, PLAYER from " + databaseTable() + " WHERE GKEY=?")) {
                    ps.setString(1, key);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int id = rs.getInt("ID");
                            String vip = rs.getString("VIP");
                            long duration = rs.getLong("DURATION");
                            String uuid = rs.getString("PLAYER");
                            int uses = rs.getInt("USES");
                            OfflinePlayer p = (uuid == null ? null : PlayerUtils.getOfflinePlayer(UUID.fromString(uuid)));
                            return new KEY(key, id, vip, duration, uses, p);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error retrieving key with token: " + key, e);
        }
        return null;
    }

    public void transferKey(int id, OfflinePlayer p) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable(), "ID", id)) {
                KEY okey = getKey(id);
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable() + " SET PLAYER=? WHERE ID=?")) {
                    String uuid = p.getUniqueId().toString();
                    ps.setString(1, uuid);
                    ps.setInt(2, id);
                    ps.executeUpdate();

                    if(zVips.cfgu.enableLogs()) {
                        LogUtils utils = LogManager.createFile(zVips.transfers);
                        utils.log("Key " + okey.getKey() + " with id " + okey.getID() + " transferred from " + (okey.getOwner() == null ? "SERVER" : okey.getOwner().getName()) + " to " + p.getName());
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error transferring key with ID: " + id + " to player: " + p.getName(), e);
        }
    }

    public List<KEY> getUsableKeys(OfflinePlayer p) {
        List<KEY> keys = new java.util.ArrayList<>();
        try (Connection con = getConnection();) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + databaseTable() + " WHERE PLAYER=?");
                 PreparedStatement ps2 = con.prepareStatement("SELECT * FROM " + databaseTable() + " WHERE PLAYER IS NULL")) {
                ps.setString(1, p.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery();
                     ResultSet rs2 = ps2.executeQuery()) {
                    while (rs.next()) {
                        String gkey = rs.getString("GKEY");
                        int id = rs.getInt("ID");
                        String vip = rs.getString("VIP");
                        long duration = rs.getLong("DURATION");
                        int uses = rs.getInt("USES");
                        keys.add(new KEY(gkey, id, vip, duration, uses, p));
                    }
                    while (rs2.next()) {
                        String gkey = rs2.getString("GKEY");
                        int id = rs2.getInt("ID");
                        String vip = rs2.getString("VIP");
                        long duration = rs2.getLong("DURATION");
                        int uses = rs2.getInt("USES");
                        keys.add(new KEY(gkey, id, vip, duration, uses, null));
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error retrieving keys for player: " + p.getName(), e);
        }
        return keys;
    }

    public List<KEY> getKeys(@Nullable OfflinePlayer p) {
        List<KEY> keys = new java.util.ArrayList<>();
        try (Connection con = getConnection();) {
            String query;
            if(p != null) {
                query = "SELECT * FROM " + databaseTable() + " WHERE PLAYER=?";
            } else {
                query = "SELECT * FROM " + databaseTable() + " WHERE PLAYER IS NULL";
            }
            try (PreparedStatement ps = con.prepareStatement(query)) {
                if (p != null) ps.setString(1, p.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String gkey = rs.getString("GKEY");
                        int id = rs.getInt("ID");
                        String vip = rs.getString("VIP");
                        long duration = rs.getLong("DURATION");
                        int uses = rs.getInt("USES");
                        keys.add(new KEY(gkey, id, vip, duration, uses, p));
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error retrieving keys" + (p != null ? " for player: " + p.getName() : ""), e);
        }
        return keys;
    }
    public List<KEY> getKeys() {
        return getKeys(null);
    }

    public void deleteKey(int id) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable(), "ID", id)) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + databaseTable() + " WHERE ID=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error deleting key with id: " + id, e);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Vips">
    public void setVIP(OfflinePlayer p, VIP vip) {
        try (Connection con = getConnection()) {
            String uuid = p.getUniqueId().toString();
            long now = System.currentTimeMillis();
            if (isInTable(databaseTable3(), "UUID", uuid, "VIP", vip.getName())) {
                updateDuration(p, vip.getName(), now);
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable3() + " SET DURATION=DURATION+? WHERE UUID=? AND VIP=?")) {
                    ps.setLong(1, vip.getDuration());
                    ps.setString(2, uuid);
                    ps.setString(3, vip.getName());
                    ps.executeUpdate();
                }
            } else {
                String query;
                if (type == database_type.H2) {
                    query = "MERGE INTO " + databaseTable3() + " (UUID, VIP, DURATION, LASTUPDATE) KEY(UUID, VIP) VALUES (?, ?, ?, ?)";
                } else {
                    query = "INSERT INTO " + databaseTable3() + " (UUID, VIP, DURATION, LASTUPDATE) VALUES (?, ?, ?)";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, uuid);
                    ps.setString(2, vip.getName());
                    ps.setLong(3, vip.getDuration());
                    ps.setLong(4, now);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding points for player: " + p.getName(), e);
        }
    }

    public long updateDuration(OfflinePlayer p, String vip, long now) {
        try (Connection con = getConnection()) {
            String uuid = p.getUniqueId().toString();
            if (isInTable(databaseTable3(), "UUID", uuid, "VIP", vip)) {
                long duration = getDuration(p, vip);
                duration -= now-getLastUpdate(p ,vip);
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable3() + " SET DURATION=? WHERE UUID=? AND VIP=?")) {
                    ps.setLong(1, duration);
                    ps.setString(2, uuid);
                    ps.setString(3, vip);
                    ps.executeUpdate();
                    setLastUpdate(p, vip, now);
                    return duration;
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding points for player: " + p.getName(), e);
        }
        return 0L;
    }

    public long getDuration(OfflinePlayer p, String vip) {
        try (Connection con = getConnection()) {
            String uuid = p.getUniqueId().toString();
            if (isInTable(databaseTable3(), "UUID", uuid, "VIP", vip)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT DURATION from " + databaseTable3() + " WHERE UUID=? AND VIP=?")) {
                    ps.setString(1, uuid);
                    ps.setString(2, vip);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getLong("DURATION");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error getting player "+p.getName()+"'s current active VIP", e);
        }
        return 0L;
    }

    public void setLastUpdate(OfflinePlayer p, String vip, long time) {
        try (Connection con = getConnection()) {
            String uuid = p.getUniqueId().toString();
            if (isInTable(databaseTable3(), "UUID", uuid, "VIP", vip)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable3() + " SET LASTUPDATE=? WHERE UUID=? AND VIP=?")) {
                    ps.setLong(1, time);
                    ps.setString(2, uuid);
                    ps.setString(3, vip);
                    ps.executeUpdate();
                }
            } else {
                String query;
                if (type == database_type.H2) {
                    query = "MERGE INTO " + databaseTable3() + " (UUID, VIP, LASTUPDATE) KEY(UUID, VIP) VALUES (?, ?, ?)";
                } else {
                    query = "INSERT INTO " + databaseTable3() + " (UUID, VIP, LASTUPDATE) VALUES (?, ?, ?)";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, uuid);
                    ps.setString(2, vip);
                    ps.setLong(3, time);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding points for player: " + p.getName(), e);
        }
    }

    public long getLastUpdate(OfflinePlayer p, String vip) {
        try (Connection con = getConnection()) {
            String uuid = p.getUniqueId().toString();
            if (isInTable(databaseTable3(), "UUID", uuid, "VIP", vip)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT LASTUPDATE from " + databaseTable3() + " WHERE UUID=? AND VIP=?")) {
                    ps.setString(1, uuid);
                    ps.setString(2, vip);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getLong("LASTUPDATE");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error getting player "+p.getName()+"'s current active VIP", e);
        }
        return 0L;
    }
    //</editor-fold>

    //<editor-fold desc="Players">
    //<editor-fold desc="Points">
    public void setPoints(OfflinePlayer p, int points) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable2(), p)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable2() + " SET POINTS=? WHERE UUID=?")) {
                    String uuid = p.getUniqueId().toString();
                    ps.setInt(1, points);
                    ps.setString(2, uuid);
                    ps.executeUpdate();
                }
            } else {
                String query;
                if (type == database_type.H2) {
                    query = "MERGE INTO " + databaseTable2() + " (UUID, POINTS) KEY(UUID) VALUES (?, ?)";
                } else {
                    query = "INSERT INTO " + databaseTable2() + " (UUID, POINTS) VALUES (?, ?)";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    String uuid = p.getUniqueId().toString();
                    ps.setString(1, uuid);
                    ps.setInt(2, points);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding points for player: " + p.getName(), e);
        }
    }

    public void addPoints(OfflinePlayer p, int points) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable2(), p)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable2() + " SET POINTS=POINTS+? WHERE UUID=?")) {
                    String uuid = p.getUniqueId().toString();
                    ps.setInt(1, points);
                    ps.setString(2, uuid);
                    ps.executeUpdate();
                }
            } else {
                String query;
                switch(type) {
                    case SQLITE:
                        query = "INSERT INTO " + databaseTable2() + " (UUID, POINTS) VALUES (?, ?) " +
                                "ON CONFLICT(UUID) DO UPDATE SET POINTS = POINTS + excluded.POINTS";
                        break;
                    case H2:
                        query = "MERGE INTO " + databaseTable2() + " (UUID,POINTS) KEY(UUID) VALUES (?, ?)";
                        break;
                    default:
                        query = "INSERT INTO " + databaseTable2() + " (UUID, POINTS) VALUES (?, ?) " +
                                "ON DUPLICATE KEY UPDATE POINTS = POINTS + VALUES(POINTS)";
                        break;
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    String uuid = p.getUniqueId().toString();
                    ps.setString(1, uuid);
                    ps.setInt(2, points);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding points for player: " + p.getName(), e);
        }
    }

    public void removePoints(OfflinePlayer p, int points) {
        try {
            if (isInTable(databaseTable2(), p)) {
                int currentPoints = getPoints(p);
                int newPoints = Math.max(0, currentPoints - points);
                setPoints(p, newPoints);
            }
        } catch (Exception e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error removing points for player: " + p.getName(), e);
        }
    }

    public void resetPoints(OfflinePlayer p) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable2(), p)) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + databaseTable2() + " WHERE UUID=?")) {
                    String uuid = p.getUniqueId().toString();
                    ps.setString(1, uuid);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error resetting points for player: " + p.getName(), e);
        }
    }

    public int getPoints(OfflinePlayer p) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable2(), p)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT POINTS from " + databaseTable2() + " WHERE UUID=?")) {
                    String uuid = p.getUniqueId().toString();
                    ps.setString(1, uuid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("POINTS");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding points for player: " + p.getName(), e);
        }
        return 0;
    }
    //</editor-fold

    //<editor-fold desc="Active">
    public void setActive(OfflinePlayer p, String vip) {
        try (Connection con = getConnection()) {
            String uuid = p.getUniqueId().toString();
            if (isInTable(databaseTable3(), "UUID", uuid, "VIP", vip)) {
                if (isInTable(databaseTable2(), p)) {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable2() + " SET ACTIVE=? WHERE UUID=?")) {
                        if (vip == null) {
                            ps.setNull(1, Types.VARCHAR);
                        } else {
                            ps.setString(1, vip);
                        }
                        ps.setString(2, uuid);
                        ps.executeUpdate();
                    }
                } else {
                    String query;
                    if (type == database_type.H2) {
                        query = "MERGE INTO " + databaseTable2() + " (UUID, ACTIVE) KEY(UUID) VALUES (?, ?)";
                    } else {
                        query = "INSERT INTO " + databaseTable2() + " (UUID, ACTIVE) VALUES (?, ?)";
                    }
                    try (PreparedStatement ps = con.prepareStatement(query)) {
                        ps.setString(1, uuid);
                        if (vip == null) {
                            ps.setNull(2, Types.VARCHAR);
                        } else {
                            ps.setString(2, vip);
                        }
                        ps.executeUpdate();
                    }
                }
                setLastUpdate(p, vip, System.currentTimeMillis());
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding points for player: " + p.getName(), e);
        }
    }

    public String getActive(OfflinePlayer p) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable2(), p)) {
                String uuid = p.getUniqueId().toString();
                try (PreparedStatement ps = con.prepareStatement("SELECT ACTIVE from " + databaseTable2() + " WHERE UUID=?")) {
                    ps.setString(1, uuid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("ACTIVE");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error getting player "+p.getName()+"'s current active VIP", e);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Activated">
    public int increaseActivated(OfflinePlayer p) {
        try (Connection con = getConnection()) {
            String uuid = p.getUniqueId().toString();
            if (isInTable(databaseTable2(), p)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable2() + " SET ACTIVATED=ACTIVATED+1 WHERE UUID=?")) {
                    ps.setString(1, uuid);
                    ps.executeUpdate();
                }
            } else {
                String query;
                if (type == database_type.H2) {
                    query = "MERGE INTO " + databaseTable2() + " (UUID, ACTIVATED) KEY(UUID) VALUES (?, 1)";
                } else {
                    query = "INSERT INTO " + databaseTable2() + " (UUID, ACTIVATED) VALUES (?, 1)";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, uuid);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error increasing current key ID", e);
        }
        return getCurrentKeyID();
    }

    public int getCurrentActivated(OfflinePlayer p) {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable2(), p)) {
                String uuid = p.getUniqueId().toString();
                try (PreparedStatement ps = con.prepareStatement("SELECT ACTIVATED from " + databaseTable2() + " WHERE UUID=?")) {
                    ps.setString(1, uuid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("ACTIVATED");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error retrieving current key ID", e);
        }
        return 0;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Party Vip">
    public void setPVPoints(int points) {
        try (Connection con = getConnection()) {
            String setting = "PARTY_VIP_POINTS";
            if (isInTable(databaseTable4(), "SETTING", setting)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable4() + " SET IVALUE=? WHERE SETTING=?")) {
                    ps.setInt(1, points);
                    ps.setString(2, setting);
                    ps.executeUpdate();
                }
            } else {
                String query;
                if (type == database_type.H2) {
                    query = "MERGE INTO " + databaseTable4() + " (SETTING, IVALUE) KEY(SETTING) VALUES (?, ?)";
                } else {
                    query = "INSERT INTO " + databaseTable4() + " (SETTING, IVALUE) VALUES (?, ?)";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, setting);
                    ps.setInt(2, points);
                    ps.executeUpdate();
                }
            }
            PartyvipUtils.checkPartyvip();
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error setting party vip points", e);
        }
    }

    public void addPVPoints(int points) {
        try (Connection con = getConnection()) {
            String setting = "PARTY_VIP_POINTS";
            if (isInTable(databaseTable4(), "SETTING", setting)) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable4() + " SET IVALUE=IVALUE+? WHERE SETTING=?")) {
                    ps.setInt(1, points);
                    ps.setString(2, setting);
                    ps.executeUpdate();
                }
                PartyvipUtils.checkPartyvip();
            } else {
                setPVPoints(points);
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error adding party vip points", e);
        }
    }

    public void removePVPoints(int points) {
        try {
            int currentPoints = getPVPoints();
            int newPoints = Math.max(0, currentPoints - points);
            setPVPoints(newPoints);
        } catch (Exception e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error removing party vip points", e);
        }
    }

    public void resetPVPoints() {
        try (Connection con = getConnection()) {
            String setting = "PARTY_VIP_POINTS";
            if (isInTable(databaseTable4(), "SETTING", setting)) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + databaseTable4() + " WHERE SETTING=?")) {
                    ps.setString(1, setting);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error resetting party vip points", e);
        }
    }

    public int getPVPoints() {
        try (Connection con = getConnection()) {
            String setting = "PARTY_VIP_POINTS";
            if (isInTable(databaseTable4(), "SETTING", setting)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT IVALUE from " + databaseTable4() + " WHERE SETTING=?")) {
                    ps.setString(1, setting);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("IVALUE");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error getting party vip points", e);
        }
        return 0;
    }
    //</editor-fold>

    //<editor-fold desc="Key ID Management">
    public int increaseCurrentKeyID() {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable4(), "SETTING", "CURRENT_KEY_ID")) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE " + databaseTable4() + " SET IVALUE=IVALUE+1 WHERE SETTING=?")) {
                    ps.setString(1, "CURRENT_KEY_ID");
                    ps.executeUpdate();
                }
            } else {
                String query;
                if (type == database_type.H2) {
                    query = "MERGE INTO " + databaseTable4() + " (SETTING, IVALUE) KEY(SETTING) VALUES (?, 1)";
                } else {
                    query = "INSERT INTO " + databaseTable4() + " (SETTING, IVALUE) VALUES (?, 1)";
                }
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, "CURRENT_KEY_ID");
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error increasing current key ID", e);
        }
        return getCurrentKeyID();
    }

    public int getCurrentKeyID() {
        try (Connection con = getConnection()) {
            if (isInTable(databaseTable4(), "SETTING", "CURRENT_KEY_ID")) {
                try (PreparedStatement ps = con.prepareStatement("SELECT IVALUE from " + databaseTable4() + " WHERE SETTING=?")) {
                    ps.setString(1, "CURRENT_KEY_ID");
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("IVALUE");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            zVips.getInstance().getLogger().log(Level.SEVERE, "Error retrieving current key ID", e);
        }
        return 1;
    }
    //</editor-fold>

    //<editor-fold desc="Table Checkers">
    public boolean isInTable(String table, OfflinePlayer p) {
        try {
            String uuid = p.getUniqueId().toString();
            if (existsTableColumnValue(table, "UUID", uuid))
                return true;
        } catch (Exception ignored) {}
        return false;
    }

    public boolean isInTable(String table, String column, String string) {
        try {
            if (existsTableColumnValue(table, column, string))
                return true;
        } catch (Exception ignored) {}
        return false;
    }

    public boolean isInTable(String table, String column, int integer) {
        try {
            if (existsTableColumnValue(table, column, integer))
                return true;
        } catch (Exception ignored) {}
        return false;
    }

    public boolean isInTable(String table, String column, String value, String column2, int integer) {
        try {
            if (existsTableColumnValueDouble(table, column, value, column2, integer))
                return true;
        } catch (Exception ignored) {}
        return false;
    }

    public boolean isInTable(String table, String column, String value, String column2, String value2) {
        try {
            if (existsTableColumnValueDouble(table, column, value, column2, value2))
                return true;
        } catch (Exception ignored) {}
        return false;
    }
    //</editor-fold>

}
