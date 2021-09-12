package de.staticred.addon.filemanager;

import de.staticred.dbv2.files.util.Updatable;

import java.io.File;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class MessageFile extends Updatable {

    public MessageFile(File current) {
        super(current, "addonfiles/messages.yml");
    }

    public String getDCNoPermission() {
        return configuration.getString("discord.noPermission");
    }

    public String getVerifyUsage() {
        return configuration.getString("command.verify.verifySyntax");
    }

    public String getPlayerNotFound() {
        return configuration.getString("command.verify.invalidPlayer");
    }

    public String alreadyVerified() {
        return configuration.getString("command.verify.alreadyVerified");
    }

    public String getAccept() {
        return convertToMcString(configuration.getString("command.verify.accept"));
    }

    public String getAcceptHover() {
        return convertToMcString(configuration.getString("command.verify.acceptHover"));
    }

    public String getDecline() {
        return convertToMcString(configuration.getString("command.verify.decline"));
    }

    public String getDeclineHover() {
        return convertToMcString(configuration.getString("command.verify.declineHover"));
    }

    public String getFiller() {
        return convertToMcString(configuration.getString("command.verify.accDeclFiller"));
    }

    public String getRequest() {
        return convertToMcString(configuration.getString("command.verify.request"));
    }

    public String getAlreadySendInquiry() {
        return configuration.getString("command.verify.alreadySendInquiry");
    }

    public String getPlayerHasInquiry() {
        return configuration.getString("command.verify.playerHasInquiry");
    }

    public String getVerifyDescription() {
        return configuration.getString("command.verify.description");
    }

    public String getNameDescription() {
        return configuration.getString("command.verify.nameDescription");
    }

    public String getRequestSent() {
        return configuration.getString("command.verify.requestSent");
    }

    public String getMCTimeOut() {
        return configuration.getString("command.verifyMC.inquiryTimeOuted");
    }
    public String getDCTimeOut() {
        return configuration.getString("command.verify.inquiryBlocked");
    }

    public String getVerifyMCSyntax() {
        return configuration.getString("command.verifyMC.usage");
    }

    public String getAcceptSubCommand() {
        return configuration.getString("command.verifyMC.acceptSubCommand");
    }


    public String getSVReloaded() {
        return configuration.getString("command.sv.reloaded");
    }

    public String getDeclineSubCommand() {
        return configuration.getString("command.verifyMC.declineSubCommand");
    }

    public String getNoInquiryPending() {
        return configuration.getString("command.verifyMC.noInquiryPending");
    }

    public String getGroupSyntax() {
        return configuration.getString("command.group.usage");
    }

    public String getDiscordJoin() {
        return configuration.getString("events.joinDiscord");
    }

    public String getMinecraftJoin() {
        return configuration.getString("events.join");
    }

    public String getHover() {
        return configuration.getString("events.hover");
    }

    public String getDeclined() {
        return configuration.getString("command.verifyMC.declined");
    }

    public String getGroupsGenerated() {
        return configuration.getString("command.group.groupsGenerated");
    }

    public String getGroupsReloaded() {
        return configuration.getString("command.group.groupsReloaded");
    }

    public String getVerifyUpdated() {
        return configuration.getString("command.verify.updated");
    }

    public String getUnlinkNotVerified() {
        return configuration.getString("command.unlink.notVerifiedYet");
    }

    public String getUnlinkPlayerNotFound() {
        return configuration.getString("command.unlink.playerNotOnline");
    }

    public String getUnlinkUnlinked() {
        return configuration.getString("command.unlink.unlinked");
    }

    public String getUpdateNotVerified() {
        return configuration.getString("command.update.notVerifiedYet");
    }

    public String getUpdatePlayerNotFound() {
        return configuration.getString("command.update.playerNotOnline");
    }

    public String getUpdateUnlinked() {
        return configuration.getString("command.update.unlinked");
    }

    public String getSVUsage() {
        return configuration.getString("command.sv.usage");
    }

    public String getSVUnlinked() {
        return configuration.getString("command.sv.unlinked");
    }

    public String getSVUpdateUsage() {
        return configuration.getString("command.sv.updateUsage");
    }

    public String getSVNotVerified() {
        return configuration.getString("command.sv.notVerified");
    }

    public String getVerifyNotVerified() {
        return configuration.getString("command.verify.notVerified");
    }

    public String getVerifyUnlinked() {
        return configuration.getString("command.verify.unlinked");
    }

    public String getSVUnlinkUsage() {
        return configuration.getString("command.sv.unlinkUsage");
    }

    public String getSVUpdated() {
        return configuration.getString("command.sv.updated");
    }

    public String getDeclinedDiscord() {
        return configuration.getString("command.verify.declined");
    }

    public String getAccepted() {
        return configuration.getString("command.verify.accepted");
    }

    public String getErrorOccurred() {
        return configuration.getString("command.verifyMC.errorOccurred");
    }

    public String getMCAccepted() {
        return configuration.getString("command.verifyMC.accepted");
    }


    public String getGroupsEmpty() {
        return configuration.getString("command.group.groupsEmpty");
    }

    private String convertToMcString(String mc) {
        return mc.replaceAll("&", "§");
    }

    public String getMCNoPermission() {
        return configuration.getString("mc.noPermission");
    }

}
