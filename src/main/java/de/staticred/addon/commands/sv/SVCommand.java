package de.staticred.addon.commands.sv;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.addon.util.SyncManager;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.commands.util.DBUCommand;
import de.staticred.dbv2.discord.util.BotHelper;
import de.staticred.dbv2.player.DBUPlayer;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class SVCommand implements DBUCommand {

    @Override
    public String getName() {
        return "sv";
    }

    @Override
    public String getPermission() {
        return "sv.cmd.sv";
    }

    @Override
    public void execute(DBUPlayer dbuPlayer, String[] strings) {
        MessageFile messageFile = VerifyAddon.getInstance().getMessageManager();

        if (!dbuPlayer.hasPermission("sv.cmd.sv")) {
            dbuPlayer.sendMessage(messageFile.getMCNoPermission());
            return;
        }

        if (strings.length < 1) {
            dbuPlayer.sendMessage(messageFile.getSVUsage());
            return;
        }

        if (strings[0].equalsIgnoreCase("unlink")) {
            if (strings.length != 2) {
                dbuPlayer.sendMessage(messageFile.getSVUnlinkUsage());
                return;
            }

            UUID uuid = UUID.fromString(strings[1]);

            try {
                if (VerifyAddon.getInstance().getVerifyDAO().isVerified(uuid)) {

                    long discordID = VerifyAddon.getInstance().getVerifyDAO().getDiscordID(uuid).get();

                    VerifyAddon.getInstance().getVerifyDAO().setDiscordID(uuid, 0);

                    VerifyAddon.getInstance().getVerifyDAO().setVerifyState(uuid, false);


                    if (VerifyAddon.getInstance().getConfigManager().getPrioritizeDiscordRoles())
                        SyncManager.removeAllRoles(BotHelper.guild.retrieveMemberById(discordID).complete());
                } else {
                    dbuPlayer.sendMessage(messageFile.getSVNotVerified());
                    return;
                }
            } catch (SQLException e) {
                DBUtil.getINSTANCE().getLogger().postError("Error while unlinking player while leaving discord");
                return;
            }

            dbuPlayer.sendMessage(messageFile.getSVUnlinked());
        } else if (strings[0].equalsIgnoreCase("update")) {
            if (strings.length != 2) {
                dbuPlayer.sendMessage(messageFile.getSVUpdateUsage());
                return;
            }

            UUID uuid = UUID.fromString(strings[1]);

            try {
                if (!VerifyAddon.getInstance().getVerifyDAO().isVerified(uuid)) {
                    dbuPlayer.sendMessage(messageFile.getSVNotVerified());
                    return;
                }

                long discordID = VerifyAddon.getInstance().getVerifyDAO().getDiscordID(uuid).get();

                SyncManager.updateRolesAndGroups(dbuPlayer, discordID);

                dbuPlayer.sendMessage(messageFile.getSVUpdated());
                return;
            } catch (SQLException ex) {
                DBUtil.getINSTANCE().getLogger().postError("Error while unlinking player while leaving discord");
                return;
            }

        } else if (strings[0].equalsIgnoreCase("reload")) {
            VerifyAddon.getInstance().getGroupsFile().reloadConfiguration();
            VerifyAddon.getInstance().getConfigManager().reloadConfiguration();
            VerifyAddon.getInstance().getMessageManager().reloadConfiguration();
            VerifyAddon.getInstance().getRewardCommandsFile().reloadConfiguration();

            dbuPlayer.sendMessage(VerifyAddon.getInstance().getMessageManager().getSVReloaded());
            return;
        } else {
            dbuPlayer.sendMessage(messageFile.getSVUnlinkUsage());
            return;
        }

        dbuPlayer.sendMessage(messageFile.getSVUnlinkUsage());
    }
}
