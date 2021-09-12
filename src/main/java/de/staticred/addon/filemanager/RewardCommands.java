package de.staticred.addon.filemanager;

import de.staticred.dbv2.files.util.DBUtilFile;

import java.io.File;
import java.util.List;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class RewardCommands extends DBUtilFile {

    public RewardCommands(File current) {
        super(current, "addonfiles/rewardCommands.yml");
    }

    @Override
    public void afterLoad() {
    }

    public List<String> getVerifyCommands() {
        return configuration.getStringList("verify.commands");
    }

    public List<String> getUnlinkCommands() {
        return configuration.getStringList("unlink.commands");
    }

}
