package de.staticred.addon.daos;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public interface RewardDAO {

    boolean wasRewarded(UUID uuid) throws SQLException;

    void setRewardState(UUID uuid, boolean state) throws SQLException;

    boolean inDatabase(UUID uuid) throws SQLException;

}
