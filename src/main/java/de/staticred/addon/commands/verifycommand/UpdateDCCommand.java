package de.staticred.addon.commands.verifycommand;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.util.SyncManager;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.commands.util.DiscordCommand;
import de.staticred.dbv2.player.DBUPlayer;
import de.staticred.dbv2.player.DiscordSender;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class UpdateDCCommand implements DiscordCommand {

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getPrefix() {
        return "!";
    }

    @Override
    public String getPermission() {
        return "sv.cmd.update";
    }

    @Override
    public void execute(DiscordSender discordSender, TextChannel textChannel, Message message, String[] strings) {
        try {
            if (!discordSender.hasPermission(getPermission())) {
                discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getDCNoPermission());
                return;
            }

            if (!VerifyAddon.getInstance().getVerifyDAO().isVerified(discordSender.getMember().getIdLong())) {
                discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getUpdatePlayerNotFound());
                return;
            }

            UUID uuid = VerifyAddon.getInstance().getVerifyDAO().getUUID(discordSender.getMember().getIdLong()).get();

            DBUPlayer player = DBUtil.getINSTANCE().getProxy().getPlayer(uuid);

            if (player == null) {
                discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getUpdatePlayerNotFound());
                return;
            }

            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getUpdateUnlinked());
            SyncManager.unlink(player, discordSender.getMember().getIdLong());
            VerifyAddon.getInstance().getVerifyDAO().setVerifyState(uuid, false);
            VerifyAddon.getInstance().getVerifyDAO().setDiscordID(uuid, 0);
        } catch (SQLException e) {
            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getErrorOccurred());
            return;
        }
    }
}
