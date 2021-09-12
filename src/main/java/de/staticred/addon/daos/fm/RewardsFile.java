package de.staticred.addon.daos.fm;

import de.staticred.addon.daos.RewardDAO;
import de.staticred.dbv2.files.util.DBUtilFile;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class RewardsFile extends DBUtilFile implements RewardDAO {

    public RewardsFile(File current) {
        super(current, "addonfiles/rewards.yml");
    }

    @Override
    public void afterLoad() {
    }

    @Override
    public boolean wasRewarded(UUID uuid) throws SQLException {
        return configuration.getBoolean(uuid.toString() + ".rewarded");
    }

    @Override
    public void setRewardState(UUID uuid, boolean state) throws SQLException {
        configuration.set(uuid.toString() + ".rewarded", state);
        saveData();
    }

    @Override
    public boolean inDatabase(UUID uuid) throws SQLException {
        return configuration.contains(uuid.toString());
    }
}
