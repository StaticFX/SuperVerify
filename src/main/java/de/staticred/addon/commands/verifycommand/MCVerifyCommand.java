package de.staticred.addon.commands.verifycommand;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.commands.verifycommand.sub.Accept;
import de.staticred.addon.commands.verifycommand.sub.Decline;
import de.staticred.addon.commands.verifycommand.sub.Unlink;
import de.staticred.addon.commands.verifycommand.sub.Update;
import de.staticred.addon.filemanager.MessageFile;
import de.staticred.addon.util.VerifyInquiryManager;
import de.staticred.dbv2.commands.util.DBUCommand;
import de.staticred.dbv2.player.DBUPlayer;

import java.sql.SQLException;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class MCVerifyCommand implements DBUCommand {

    @Override
    public String getName() {
        return VerifyAddon.getInstance().getConfigManager().getVerifyCommand();
    }

    @Override
    public String getPermission() {
        return "sv.cmd.verify";
    }

    @Override
    public void execute(DBUPlayer dbuPlayer, String[] args) {

        MessageFile messageFile = VerifyAddon.getInstance().getMessageManager();

        if (!dbuPlayer.hasPermission(getPermission())) {
            dbuPlayer.sendMessage(messageFile.getDCNoPermission());
            return;
        }

        if (args.length != 1) {
            dbuPlayer.sendMessage(messageFile.getVerifyMCSyntax());
            return;
        }



        String subCommand = args[0];

        if (subCommand.equalsIgnoreCase("accept")) {
            try {
                new Accept(dbuPlayer, args);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (subCommand.equalsIgnoreCase("decline")) {
            new Decline(dbuPlayer, args);
        } else if (subCommand.equals("update")) {
            try {
                new Update(dbuPlayer, args);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (subCommand.equals("unlink")) {
            try {
                new Unlink(dbuPlayer, args);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            dbuPlayer.sendMessage(messageFile.getVerifyMCSyntax());
        }
    }
}
