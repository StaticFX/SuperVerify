package de.staticred.addon.util.tasks;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.discord.util.BotHelper;
import de.staticred.dbv2.discord.util.Embed;
import de.staticred.dbv2.player.DBUPlayer;
import de.staticred.dbv2.util.DoubleOptional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;

import java.awt.Color;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class InquiryRemover extends TimerTask {

    private final long discordID;

    public InquiryRemover(long discordID) {
        this.discordID = discordID;
    }

    @Override
    public void run() {


        if (!VerifyAddon.getInstance().getVerifyInquiryManager().hasSendInquiry(discordID))
            return;

        DBUPlayer player = VerifyAddon.getInstance().getVerifyInquiryManager().getPlayer(discordID);
        DoubleOptional<Message, InteractionHook> messageDouble = VerifyAddon.getInstance().getVerifyInquiryManager().getDoubleOptional(discordID);
        VerifyAddon.getInstance().getVerifyInquiryManager().removeFromInquiryList(discordID);
        MessageFile msgm = VerifyAddon.getInstance().getMessageManager();


        if (messageDouble.isAPresent()) {
            EmbedBuilder messageEmbed = new EmbedBuilder(messageDouble.getA().getEmbeds().get(0));
            messageEmbed.setColor(Color.RED);
            messageEmbed.setDescription(msgm.getDCTimeOut());
            if (DBUtil.getINSTANCE().getConfigFileManager().deleteTime() > 0)
                messageDouble.getA().getTextChannel().sendMessage(messageEmbed.build()).queue(m -> m.delete().queueAfter(DBUtil.getINSTANCE().getConfigFileManager().deleteTime(), TimeUnit.SECONDS));
            else
                messageDouble.getA().getTextChannel().sendMessage(messageEmbed.build()).queue();
        } else if (messageDouble.isBPresent()) {
            EmbedBuilder messageEmbed = new EmbedBuilder();
            messageEmbed.setThumbnail(BotHelper.guild.getMemberById(discordID).getUser().getAvatarUrl());
            messageEmbed.setColor(Color.RED);
            messageEmbed.setDescription(msgm.getDCTimeOut());
            messageDouble.getB().editOriginalEmbeds(messageEmbed.build()).queue();
        } else {
            DBUtil.getINSTANCE().getErrorLogger().postError("Error while trying to edit message embed from " + player.getName());
            return;
        }


        player.sendMessage(msgm.getMCTimeOut());

    }
}
