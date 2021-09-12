package de.staticred.addon.commands.verifycommand.sub;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.daos.VerifyDAO;
import de.staticred.addon.filemanager.ConfigFile;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.addon.util.VerifyInquiryManager;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.discord.util.BotHelper;
import de.staticred.dbv2.player.DBUPlayer;
import de.staticred.dbv2.util.DoubleOptional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.Color;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class Decline {

    public Decline(DBUPlayer dbuPlayer, String[] args) {

        VerifyDAO dao = VerifyAddon.getInstance().getVerifyDAO();
        MessageFile msg = VerifyAddon.getInstance().getMessageManager();
        VerifyInquiryManager manager = VerifyAddon.getInstance().getVerifyInquiryManager();
        ConfigFile config = VerifyAddon.getInstance().getConfigManager();

        if (!manager.someoneSendToPlayer(dbuPlayer)) {
            dbuPlayer.sendMessage(msg.getNoInquiryPending());
            return;
        }


        dbuPlayer.sendMessage(VerifyAddon.getInstance().getMessageManager().getDeclined());

        DoubleOptional<Message, InteractionHook> pair = VerifyAddon.getInstance().getVerifyInquiryManager().getDoubleOptional(VerifyAddon.getInstance().getVerifyInquiryManager().getID(dbuPlayer));

        if (pair.isAPresent()) {
            Message m = pair.getA();

            EmbedBuilder messageEmbed = new EmbedBuilder(m.getEmbeds().get(0));
            messageEmbed.setColor(Color.RED);
            messageEmbed.setDescription(VerifyAddon.getInstance().getMessageManager().getDeclinedDiscord());
            pair.getA().getTextChannel().sendMessage(messageEmbed.build()).queue();
        } else if (pair.isBPresent()) {
            EmbedBuilder messageEmbed = new EmbedBuilder();
            messageEmbed.setThumbnail(BotHelper.guild.getMemberById(VerifyAddon.getInstance().getVerifyInquiryManager().getID(dbuPlayer)).getUser().getAvatarUrl());
            messageEmbed.setColor(Color.RED);
            messageEmbed.setDescription(VerifyAddon.getInstance().getMessageManager().getDeclinedDiscord());
            pair.getB().editOriginalEmbeds(messageEmbed.build()).queue();
        } else {
            DBUtil.getINSTANCE().getErrorLogger().postError("Error while trying to edit message embed from " + dbuPlayer.getName());
            return;
        }

        VerifyAddon.getInstance().getVerifyInquiryManager().removeFromInquiryList(VerifyAddon.getInstance().getVerifyInquiryManager().getID(dbuPlayer));
    }


}
