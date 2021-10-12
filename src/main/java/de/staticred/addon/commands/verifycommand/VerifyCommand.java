package de.staticred.addon.commands.verifycommand;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.daos.VerifyDAO;
import de.staticred.addon.util.tasks.InquiryRemover;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.adventure.text.Component;
import de.staticred.dbv2.adventure.text.TextComponent;
import de.staticred.dbv2.adventure.text.event.ClickEvent;
import de.staticred.dbv2.adventure.text.event.HoverEvent;
import de.staticred.dbv2.commands.util.DiscordCommand;
import de.staticred.dbv2.player.DBUPlayer;
import de.staticred.dbv2.player.DiscordSender;
import de.staticred.dbv2.util.DoubleOptional;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.sql.SQLException;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class VerifyCommand implements DiscordCommand {

    public static final String NAME = "verify";
    public static final String PREFIX = DBUtil.getINSTANCE().getConfigFileManager().getPrefix();
    public static final String PERMISSION = "dv.cmd.verify";

    public String getName() {
        return NAME;
    }

    public String getPrefix() {
        return PREFIX;
    }

    public String getPermission() {
        return PERMISSION;
    }

    public void execute(DiscordSender discordSender, TextChannel textChannel, Message message, String[] args) {
        if (!discordSender.hasPermission(PERMISSION)) {
            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getDCNoPermission());
            return;
        }

        if (args.length != 1) {
            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getVerifyUsage().replace("{prefix}", DBUtil.getINSTANCE().getConfigFileManager().getPrefix()));
            return;
        }

        String ingameName = args[0];

        DBUPlayer player = DBUtil.getINSTANCE().getProxy().getOnlinePlayer(ingameName);

        if (player == null) {
            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getPlayerNotFound());
            return;
        }

        VerifyDAO dao = VerifyAddon.getInstance().getVerifyDAO();

        try {
            if (dao.isVerified(player.getUUID())) {
                discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().alreadyVerified());
                return;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }



        if (VerifyAddon.getInstance().getVerifyInquiryManager().hasSendInquiry(discordSender.getMember().getIdLong())) {
            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getAlreadySendInquiry());
            return;
        }

        if (VerifyAddon.getInstance().getVerifyInquiryManager().someoneSendToPlayer(player)) {
            discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getPlayerHasInquiry());
            return;
        }

        player.sendMessage(VerifyAddon.getInstance().getMessageManager().getRequest().replaceAll("%member%", discordSender.getMember().getEffectiveName()));

        TextComponent accept = Component.text(VerifyAddon.getInstance().getMessageManager().getAccept());

        accept = accept.hoverEvent(HoverEvent.showText(Component.text(VerifyAddon.getInstance().getMessageManager().getAcceptHover())));
        accept = accept.clickEvent(ClickEvent.runCommand("/dbu " + VerifyAddon.getInstance().getConfigManager().getVerifyCommand() + " accept"));

        accept = accept.append(Component.text(VerifyAddon.getInstance().getMessageManager().getFiller()));

        TextComponent decline = Component.text(VerifyAddon.getInstance().getMessageManager().getDecline());

        decline = decline.hoverEvent(HoverEvent.showText(Component.text(VerifyAddon.getInstance().getMessageManager().getDeclineHover())));
        decline = decline.clickEvent(ClickEvent.runCommand("/dbu " + VerifyAddon.getInstance().getConfigManager().getVerifyCommand() + " decline"));

        player.sendComponent(accept.append(decline));

        DoubleOptional<Message, InteractionHook> actionSent = discordSender.sendMessage(VerifyAddon.getInstance().getMessageManager().getRequestSent());
        VerifyAddon.getInstance().getVerifyInquiryManager().queueInquiry(discordSender.getMember().getIdLong(), player, actionSent);

        int verifyTime = VerifyAddon.getInstance().getConfigManager().getVerifyTime();

        if (verifyTime > 0) {
            VerifyAddon.getInstance().getVerifyInquiryTimer().schedule(new InquiryRemover(discordSender.getMember().getIdLong()), verifyTime * 1000);
        }
    }
}
