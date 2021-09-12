package de.staticred.addon.commands.verifycommand.sub;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.daos.RewardDAO;
import de.staticred.addon.daos.VerifyDAO;
import de.staticred.addon.filemanager.ConfigFile;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.addon.util.SyncManager;
import de.staticred.addon.util.VerifyInquiryManager;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.discord.util.BotHelper;
import de.staticred.dbv2.player.DBUPlayer;
import de.staticred.dbv2.util.DoubleOptional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.Color;
import java.sql.SQLException;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class Accept {

    public Accept(DBUPlayer dbuPlayer, String[] args) throws SQLException {

        VerifyDAO dao = VerifyAddon.getInstance().getVerifyDAO();
        MessageFile msg = VerifyAddon.getInstance().getMessageManager();
        VerifyInquiryManager manager = VerifyAddon.getInstance().getVerifyInquiryManager();
        ConfigFile config = VerifyAddon.getInstance().getConfigManager();

        if (!manager.someoneSendToPlayer(dbuPlayer)) {
            dbuPlayer.sendMessage(msg.getNoInquiryPending());
            return;
        }

        if (dao.isVerified(dbuPlayer.getUUID())) {
            dbuPlayer.sendMessage(msg.alreadyVerified());
            return;
        }

        long discordid = manager.getID(dbuPlayer);

        SyncManager.updateRolesAndGroups(dbuPlayer, discordid);

        DoubleOptional<Message, InteractionHook> pair = manager.getDoubleOptional(discordid);

        if (pair.isAPresent()) {
            Message m = pair.getA();

            EmbedBuilder messageEmbed = new EmbedBuilder(m.getEmbeds().get(0));
            messageEmbed.setColor(Color.GREEN);
            messageEmbed.setDescription(msg.getAccepted());
            pair.getA().getTextChannel().sendMessage(messageEmbed.build()).queue();
        } else if (pair.isBPresent()) {
            EmbedBuilder messageEmbed = new EmbedBuilder();
            messageEmbed.setThumbnail(BotHelper.guild.getMemberById(discordid).getUser().getAvatarUrl());
            messageEmbed.setColor(Color.GREEN);
            messageEmbed.setDescription(msg.getAccepted());
            pair.getB().editOriginalEmbeds(messageEmbed.build()).queue();
        } else {
            DBUtil.getINSTANCE().getErrorLogger().postError("Error while trying to edit message embed from " + dbuPlayer.getName());
            return;
        }

        manager.removeFromInquiryList(discordid);

        VerifyAddon.getInstance().getVerifyDAO().setVerifyState(dbuPlayer.getUUID(), true);
        VerifyAddon.getInstance().getVerifyDAO().setDiscordID(dbuPlayer.getUUID(), discordid);

        dbuPlayer.sendMessage(msg.getMCAccepted());

        RewardDAO rewardDAO = VerifyAddon.getInstance().getRewardDAO();

        if (rewardDAO.wasRewarded(dbuPlayer.getUUID()) && !VerifyAddon.getInstance().getConfigManager().ignoreRewardState()) {
            return;
        }

        for (String command : VerifyAddon.getInstance().getRewardCommandsFile().getVerifyCommands()) {
            DBUtil.getINSTANCE().getProxy().executeConsoleCommand(command.replaceAll("%player%", dbuPlayer.getName()));
        }

        rewardDAO.setRewardState(dbuPlayer.getUUID(), true);

    }

}
