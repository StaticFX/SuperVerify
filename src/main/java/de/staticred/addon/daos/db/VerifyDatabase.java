package de.staticred.addon.daos.db;

import de.staticred.addon.daos.VerifyDAO;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.networking.DAO;
import de.staticred.dbv2.networking.db.DataBaseConnector;
import de.staticred.dbv2.player.DBUPlayer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Verify database.
 *
 * @author Devin Fritz
 * @version 1.0.0
 */
public class VerifyDatabase implements DAO, VerifyDAO {

    private static final String TABLE_NAME = "verify_data";
    private static final String PLAYER_UUID = "player_UUID";
    private static final String PLAYER_DISCORDID = "PLAYER_DISCORID";
    private static final String PLAYER_NAME = "PLAYER_NAME";
    private static final String PLAYER_VERIFIED = "PLAYER_VERIFIED";


    private DataBaseConnector con;

    /**
     * Instantiates a new Verify database.
     */
    public VerifyDatabase() throws SQLException {
        con = DBUtil.getINSTANCE().getDataBaseConnector();
        loadTable();
    }

    private void loadTable() throws SQLException {
        con.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_NAME  +"(" + PLAYER_UUID + " varchar(36), " + PLAYER_DISCORDID + " LONG, " + PLAYER_NAME + " VARCHAR(16), " + PLAYER_VERIFIED + " BOOLEAN)");
    }


    @Override
    public boolean isVerified(UUID uuid) throws SQLException {
        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT " + PLAYER_VERIFIED + " from " + TABLE_NAME + " WHERE " + PLAYER_UUID + " = ?");
        ps.setString(1, uuid.toString());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            boolean verified = rs.getBoolean(PLAYER_VERIFIED);

            connection.close();
            rs.close();
            ps.close();
            return verified;
        }

        connection.close();
        rs.close();
        ps.close();
        return false;
    }

    @Override
    public boolean isVerified(long discordID) throws SQLException {
        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT " + PLAYER_VERIFIED + " from " + TABLE_NAME + " WHERE " + PLAYER_DISCORDID + " = ?");
        ps.setLong(1, discordID);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            boolean verified = rs.getBoolean(PLAYER_VERIFIED);

            connection.close();
            rs.close();
            ps.close();
            return verified;
        }

        connection.close();
        rs.close();
        ps.close();
        return false;
    }

    @Override
    public Optional<Long> getDiscordID(UUID uuid) throws SQLException {
        if (!isVerified(uuid))
            return Optional.empty();

        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT " + PLAYER_DISCORDID + " from " + TABLE_NAME + " WHERE " + PLAYER_UUID + " = ?");
        ps.setString(1, uuid.toString());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            long id = rs.getLong(PLAYER_DISCORDID);

            connection.close();
            rs.close();
            ps.close();
            return Optional.of(id);
        }

        connection.close();
        rs.close();
        ps.close();

        DBUtil.getINSTANCE().getErrorLogger().postError("Error while getting discordID from: " + uuid.toString() + " this can only occur when verify state is wrongly false.");
        return Optional.empty();
    }

    @Override
    public void setDiscordID(UUID uuid, long discordID) throws SQLException {
        con.executeUpdate("UPDATE " + TABLE_NAME + " SET " + PLAYER_DISCORDID + " = ? WHERE " + PLAYER_UUID + " = ?", discordID, uuid.toString());
    }

    @Override
    public void setVerifyState(UUID uuid, boolean state) throws SQLException {
        con.executeUpdate("UPDATE " + TABLE_NAME + " SET " + PLAYER_VERIFIED + " = ? WHERE " + PLAYER_UUID + " = ?", state, uuid.toString());
    }

    @Override
    public Optional<String> getName(UUID uuid) throws SQLException {
        if (isInDataBase(uuid)) {
            addPlayerToDatabase(uuid);
        }

        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT " + PLAYER_DISCORDID + " from " + TABLE_NAME + " WHERE " + PLAYER_UUID + " = ?");
        ps.setString(1, uuid.toString());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String name = rs.getString(PLAYER_NAME);

            connection.close();
            rs.close();
            ps.close();
            return Optional.of(name);
        }

        connection.close();
        rs.close();
        ps.close();

        DBUtil.getINSTANCE().getErrorLogger().postError("Error while getting discordID from: " + uuid.toString() + " this can only occur when verify state is wrongly false.");
        return Optional.empty();
    }

    private void addPlayerToDatabase(UUID uuid) throws SQLException {
        DBUPlayer player = DBUtil.getINSTANCE().getProxy().getPlayer(uuid);

        if (player == null) {
            DBUtil.getINSTANCE().getErrorLogger().postError("Error while registering player in database. Player not found.");
            return;
        }

        if (isInDataBase(uuid))
            return;

        con.executeUpdate("INSERT INTO " + TABLE_NAME + "(" + PLAYER_UUID + "," + PLAYER_DISCORDID + "," + PLAYER_NAME + "," + PLAYER_VERIFIED+") VALUES(?,?,?,?)", uuid.toString(), null, player.getName(), false);
    }

    private boolean isInDataBase(UUID uuid) throws SQLException {
        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT " + PLAYER_UUID + " from " + TABLE_NAME + " WHERE " + PLAYER_UUID + " = ?");
        ps.setString(1, uuid.toString());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            long id = rs.getLong(PLAYER_DISCORDID);

            connection.close();
            rs.close();
            ps.close();
            return true;
        }

        connection.close();
        rs.close();
        ps.close();

        return false;
    }

    private boolean isInDatabase(long discordID) throws SQLException {
        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT " + PLAYER_DISCORDID + " from " + TABLE_NAME + " WHERE " + PLAYER_DISCORDID + " = ?");
        ps.setLong(1, discordID);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            connection.close();
            rs.close();
            ps.close();
            return true;
        }

        connection.close();
        rs.close();
        ps.close();
        return false;
    }


    @Override
    public void setName(UUID uuid, String name) throws SQLException {
        if (!isInDataBase(uuid)) {
            addPlayerToDatabase(uuid);
        }

        con.executeUpdate("UPDATE " + TABLE_NAME + " SET " + PLAYER_NAME + " = ? WHERE " + PLAYER_UUID + " = ?", name, uuid.toString());
    }

    @Override
    public Optional<UUID> getUUID(long discordID) throws SQLException {
        if (!isInDatabase(discordID)) {
            return Optional.empty();
        }

        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT " + PLAYER_UUID + " from " + TABLE_NAME + " WHERE " + PLAYER_DISCORDID + " = ?");
        ps.setLong(1,discordID);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            UUID uuid = UUID.fromString(rs.getString(PLAYER_UUID));

            connection.close();
            rs.close();
            ps.close();
            return Optional.of(uuid);
        }

        connection.close();
        rs.close();
        ps.close();

        return Optional.empty();
    }

    @Override
    public boolean startDAO() throws IOException {
        return true;
    }

    @Override
    public void shutDown() {
    }

    @Override
    public boolean saveData() throws IOException {
        return true;
    }
}
