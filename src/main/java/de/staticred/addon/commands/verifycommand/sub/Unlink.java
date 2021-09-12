package de.staticred.addon.commands.verifycommand.sub;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.daos.VerifyDAO;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.addon.util.SyncManager;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.player.DBUPlayer;

import java.sql.SQLException;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class Unlink {


    public Unlink(DBUPlayer player, String[] args) throws SQLException {

        VerifyDAO dao = VerifyAddon.getInstance().getVerifyDAO();
        MessageFile msg = VerifyAddon.getInstance().getMessageManager();

        if (!dao.isVerified(player.getUUID())) {
            player.sendMessage(msg.getVerifyNotVerified());
            return;
        }

        dao.setVerifyState(player.getUUID(), false);

        SyncManager.unlink(player, dao.getDiscordID(player.getUUID()).get());

        dao.setDiscordID(player.getUUID(), 0);

        player.sendMessage(msg.getVerifyUnlinked());

        for (String command : VerifyAddon.getInstance().getRewardCommandsFile().getUnlinkCommands()) {
            DBUtil.getINSTANCE().getProxy().executeConsoleCommand(command.replaceAll("%player%", player.getName()));
        }

    }
}
