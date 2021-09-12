package de.staticred.addon.filemanager;

import de.staticred.addon.util.Group;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.files.util.DBUtilFile;
import de.staticred.dbv2.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class GroupsFile extends DBUtilFile {


    public GroupsFile(File current) {
        super(current, "addonfiles/groups.yml");
    }

    @Override
    public void afterLoad() {
    }


    public void generateGroups() {
        reloadConfiguration();
        List<String> groups = configuration.getStringList("groups");

        for (String group : groups) {
            if (configuration.contains(group))
                continue;


            configuration.set(group + ".groupMC", group);
            configuration.set(group + ".roleID", "0");
            configuration.set(group + ".permissions", "perm." + group);
            configuration.set(group + ".nickname",  group + " | %player% ");
            configuration.set(group + ".dynamicgroup",  false);
            saveData();
        }
        reloadConfiguration();
    }

    public List<Group> getAllGroups() {
        List<String> groups = configuration.getStringList("groups");
        List<Group> returnGroups = new ArrayList<>();

        for (String group : groups) {

            Group groupObject = new Group(group, configuration.getString(group + ".roleID"), configuration.getString(group + ".groupMC"), configuration.getString(group + ".permissions"),
                    configuration.getString(group + ".nickname"), configuration.getBoolean(group + ".dynamicgroup"));
            returnGroups.add(groupObject);
        }
        return returnGroups;
    }

    public Group getGroupForID(long id) {
        Logger logger = DBUtil.getINSTANCE().getLogger();
        logger.postDebug("Getting group for id: " + id);
        for (Group group : getAllGroups()) {
            if (group.getDiscordID().equals(String.valueOf(id))) {
                logger.postDebug("Returning group: " + group.getName());
                return group;
            }
        }
        logger.postDebug("Returning null");
        return null;

    }

    public boolean roleRegistered(long id) {
        return getAllGroups().stream().anyMatch(group -> group.getDiscordID().equals(String.valueOf(id)));
    }


}
