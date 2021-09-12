package de.staticred.addon.filemanager;

import de.staticred.dbv2.files.util.Updatable;

import java.io.File;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class ConfigFile extends Updatable {

    public ConfigFile(File current) {
        super(current, "addonfiles/config.yml");
    }

    public int getVerifyTime() {
        return configuration.getInt("verifyTime");
    }

    public String getVerifyCommand () {
        return configuration.getString("verifyCommand");
    }

    public boolean getPrioritizeDiscordRoles () {
        return configuration.getBoolean("prioritizeDiscordRoles");
    }

    public boolean hasVerifyGroup() {
        return !configuration.getString("verifyRole").isEmpty();
    }


    public boolean updateUsersOnJoin() {
        return configuration.getBoolean("updateUsersOnJoin");
    }

    public String getDiscordURL() {
        return configuration.getString("discordURL");
    }

    public String getVerifyRole() {
        return configuration.getString("verifyRole");
    }

    public boolean getSyncUserNames() {
        return configuration.getBoolean("syncUserNames");
    }

    public boolean ignoreRewardState() {
        return configuration.getBoolean("ignoreRewardState");
    }

    public String getAddGroupCommand() {
        return configuration.getString("setCommand");
    }

    public String getRemoveGroupCommand() {
        return configuration.getString("removeCommand");
    }


    public boolean assignRoleOnJoin() {
        return configuration.getString("assignAsJoin").isEmpty();
    }

    public String getAssignRoleOnJoin() {
        return configuration.getString("assignAsJoin");
    }

}
