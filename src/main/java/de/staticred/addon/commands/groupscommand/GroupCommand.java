package de.staticred.addon.commands.groupscommand;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.addon.util.Group;
import de.staticred.dbv2.commands.util.DBUCommand;
import de.staticred.dbv2.player.DBUPlayer;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class GroupCommand implements DBUCommand {

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public String getPermission() {
        return "sv.cmd.group";
    }

    @Override
    public void execute(DBUPlayer dbuPlayer, String[] strings) {
        MessageFile messageFile = VerifyAddon.getInstance().getMessageManager();

        if (!dbuPlayer.hasPermission("sv.cmd.group")) {
            dbuPlayer.sendMessage(messageFile.getMCNoPermission());
            return;
        }

        if (strings.length != 1) {
            dbuPlayer.sendMessage(messageFile.getGroupSyntax());
            return;
        }

        if (strings[0].equalsIgnoreCase("list")) {
            if (VerifyAddon.getInstance().getGroupsFile().getAllGroups().size() == 0) {
                dbuPlayer.sendMessage(messageFile.getGroupsEmpty());
                return;
            }
            for (Group group : VerifyAddon.getInstance().getGroupsFile().getAllGroups()) {
                dbuPlayer.sendMessage("&7Group > &e" + group.getName());
                dbuPlayer.sendMessage("   &7DiscordID > &e" + group.getDiscordID());
                dbuPlayer.sendMessage("   &7MinecraftGroup > &e" + group.getMcGroup());
                dbuPlayer.sendMessage("   &7Permission > &e" + group.getPermission());
                dbuPlayer.sendMessage("   &7Nickname > &e" + group.getNickname());
            }
        } else if (strings[0].equalsIgnoreCase("generate")) {
            VerifyAddon.getInstance().getGroupsFile().generateGroups();
            dbuPlayer.sendMessage(messageFile.getGroupsGenerated());

        } else if (strings[0].equalsIgnoreCase("reload")) {
            VerifyAddon.getInstance().getGroupsFile().reloadConfiguration();
            dbuPlayer.sendMessage(messageFile.getGroupsReloaded());
        } else {
            dbuPlayer.sendMessage(messageFile.getGroupSyntax());
        }
    }
}
