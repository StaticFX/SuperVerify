package de.staticred.addon.daos.db;

import de.staticred.addon.daos.RewardDAO;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.networking.db.DataBaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class RewardDatabase implements RewardDAO {


    private DataBaseConnector con;

    /**
     * Instantiates a new Verify database.
     */
    public RewardDatabase() throws SQLException {
        con = DBUtil.getINSTANCE().getDataBaseConnector();
        loadTable();
    }

    private void loadTable() throws SQLException {
        con.executeUpdate("CREATE TABLE IF NOT EXISTS rewards(player_uuid VARCHAR(36) PRIMARY KEY, player_state BOOLEAN)");
    }

    @Override
    public boolean wasRewarded(UUID uuid) throws SQLException {

        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT player_state FROM rewards WHERE player_uuid = ?");
        ps.setString(1, uuid.toString());


        ResultSet rs = ps.executeQuery();
        boolean rewarded = false;

        if (rs.next()) {
            rewarded = rs.getBoolean("player_state");
        }

        connection.close();
        rs.close();
        ps.close();

        return rewarded;
    }

    @Override
    public void setRewardState(UUID uuid, boolean state) throws SQLException {
        if (!inDatabase(uuid)) {
            con.executeUpdate("INSERT INTO rewards(player_uuid, player_state) VALUES(?, ?)", uuid.toString(), false);
        }

        con.executeUpdate("UPDATE rewards SET player_state = ? WHERE player_uuid = ?", state, uuid.toString());
    }

    @Override
    public boolean inDatabase(UUID uuid) throws SQLException {
        Connection connection = con.getNewConnection();

        PreparedStatement ps = connection.prepareStatement("SELECT player_state FROM rewards WHERE player_uuid = ?");
        ps.setString(1, uuid.toString());


        ResultSet rs = ps.executeQuery();
        boolean rewarded = false;

        if (rs.next()) {
            rewarded = true;
        }

        connection.close();
        rs.close();
        ps.close();

        return rewarded;
    }
}
