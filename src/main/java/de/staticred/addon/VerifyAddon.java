package de.staticred.addon;

import de.staticred.addon.commands.groupscommand.GroupCommand;
import de.staticred.addon.commands.sv.SVCommand;
import de.staticred.addon.commands.verifycommand.MCVerifyCommand;
import de.staticred.addon.commands.verifycommand.UnlinkDCCommand;
import de.staticred.addon.commands.verifycommand.UpdateDCCommand;
import de.staticred.addon.commands.verifycommand.VerifyCommand;
import de.staticred.addon.daos.RewardDAO;
import de.staticred.addon.daos.VerifyDAO;
import de.staticred.addon.daos.db.RewardDatabase;
import de.staticred.addon.daos.db.VerifyDatabase;
import de.staticred.addon.daos.fm.RewardsFile;
import de.staticred.addon.daos.fm.VerifyFile;
import de.staticred.addon.events.DiscordMemberJoined;
import de.staticred.addon.events.DiscordMemberLeft;
import de.staticred.addon.events.MCJoinEvent;
import de.staticred.addon.filemanager.ConfigFile;
import de.staticred.addon.filemanager.GroupsFile;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.addon.filemanager.RewardCommands;
import de.staticred.addon.util.VerifyInquiryManager;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.addon.Addon;
import de.staticred.dbv2.addon.AddonInfo;
import de.staticred.dbv2.commands.util.CommandManager;
import de.staticred.dbv2.discord.util.BotHelper;
import de.staticred.dbv2.util.Logger;
import de.staticred.dbv2.util.Mode;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.sql.SQLException;
import java.util.Timer;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class VerifyAddon extends Addon {

    private static VerifyAddon instance;

    private MessageFile messageManager;
    private ConfigFile configManager;
    private VerifyFile verifyFile;
    private GroupsFile groupsFile;
    private RewardsFile rewardsFile;
    private RewardCommands rewardCommandsFile;

    private Timer verifyInquiryTimer;

    private VerifyInquiryManager verifyInquiryManager;

    private VerifyDAO verifyDAO;
    private RewardDAO rewardDAO;

    public VerifyAddon(AddonInfo info, File dataFolder, Logger logger, CommandManager commandManager, Mode mode, ClassLoader classLoader) {
        super(info, dataFolder, logger, commandManager, mode, classLoader);
    }

    public void onStart() {
        instance = this;
        getLogger().postMessage("Loading resources...");
        verifyInquiryTimer = new Timer("verifyInquiryRemover");

        if (!loadResources()) {
            getLogger().postError("Error while loading resources, stopping addon.");
            return;
        }

        verifyInquiryManager = new VerifyInquiryManager();

        getFileHelper().registerManager(configManager);
        getFileHelper().registerManager(messageManager);

        registerEvents();

        getCommandManager().registerDBUCommand(new MCVerifyCommand());
        getCommandManager().registerDBUCommand(new GroupCommand());
        getCommandManager().registerDBUCommand(new SVCommand());
        getCommandManager().registerDiscordCommand(new VerifyCommand());
        getCommandManager().registerDiscordCommand(new UnlinkDCCommand());
        getCommandManager().registerDiscordCommand(new UpdateDCCommand());
        BotHelper.registerNewCommand(new CommandData("verify", messageManager.getVerifyDescription()).addOption(new OptionData(OptionType.STRING ,"minecraft_name", messageManager.getNameDescription()).setRequired(true)));
        BotHelper.registerNewCommand(new CommandData("update", messageManager.getVerifyDescription()));
        BotHelper.registerNewCommand(new CommandData("unlink", messageManager.getVerifyDescription()));

    }

    public void onEnd() {
    }

    private void registerEvents() {
        BotHelper.registerEvent(new DiscordMemberJoined());
        BotHelper.registerEvent(new DiscordMemberLeft());

        DBUtil.getINSTANCE().getEventManager().registerEvent(new MCJoinEvent());
    }

    public Timer getVerifyInquiryTimer() {
        return verifyInquiryTimer;
    }

    private boolean loadResources() {
        configManager = new ConfigFile(new File(getDataFolder(), "config.yml"));

        messageManager = new MessageFile(new File(getDataFolder(), "messages.yml"));
        groupsFile = new GroupsFile(new File(getDataFolder(), "groups.yml"));
        rewardCommandsFile = new RewardCommands(new File(getDataFolder(), "rewardCommands.yml"));

        if (DBUtil.getINSTANCE().getConfigFileManager().useSQL()) {
            try {
                verifyDAO = new VerifyDatabase();
                rewardDAO = new RewardDatabase();
            } catch (SQLException exception) {
                exception.printStackTrace();
                getLogger().postError("Error while loading database, not starting addon.");
                return false;
            }
        } else {
            verifyFile = new VerifyFile(new File(getDataFolder(), "verify.yml"));
            rewardsFile = new RewardsFile(new File(getDataFolder(), "rewards.yml"));
            getFileHelper().registerManager(verifyFile);
            verifyDAO = verifyFile;
            rewardDAO = rewardsFile;
        }
        return true;
    }

    public RewardCommands getRewardCommandsFile() {
        return rewardCommandsFile;
    }

    public VerifyInquiryManager getVerifyInquiryManager() {
        return verifyInquiryManager;
    }

    public VerifyDAO getVerifyDAO() {
        return verifyDAO;
    }

    public GroupsFile getGroupsFile() {
        return groupsFile;
    }

    public RewardDAO getRewardDAO() {
        return rewardDAO;
    }

    public static VerifyAddon getInstance() {
        return instance;
    }

    public MessageFile getMessageManager() {
        return messageManager;
    }

    public ConfigFile getConfigManager() {
        return configManager;
    }
}
