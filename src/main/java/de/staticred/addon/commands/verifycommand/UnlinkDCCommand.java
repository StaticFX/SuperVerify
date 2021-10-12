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
public class UnlinkDCCommand implements DiscordCommand {

    public UnlinkDCCommand() {
    }

    @Override
    public String getName() {
        return "unlink";
    }

    @Override
    public String getPrefix() {
        return DBUtil.getINSTANCE().getConfigFileManager().getPrefix();
    }

    @Override
    public String getPermission() {
        return "sv.cmd.unlink";
    }

    @Override
    public void execute(DiscordSender discordSender, TextChannel textChannel, Message message, String[] strings) {
        try {
            if (!discordSender.hasPermission(getPermission())) {
                discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getDCNoPermission());
                return;
            }

            if (!VerifyAddon.getInstance().getVerifyDAO().isVerified(discordSender.getMember().getIdLong())) {
                discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getUnlinkNotVerified());
                return;
            }

            UUID uuid = VerifyAddon.getInstance().getVerifyDAO().getUUID(discordSender.getMember().getIdLong()).get();

            DBUPlayer player = DBUtil.getINSTANCE().getProxy().getPlayer(uuid);

            if (player == null) {
                discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getUnlinkPlayerNotFound());
                return;
            }

            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getUnlinkUnlinked());
            SyncManager.unlink(player, discordSender.getMember().getIdLong());
            VerifyAddon.getInstance().getVerifyDAO().setVerifyState(uuid, false);
            VerifyAddon.getInstance().getVerifyDAO().setDiscordID(uuid, 0);
        } catch (SQLException e) {
            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getErrorOccurred());
            return;
        }
    }
}
