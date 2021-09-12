package de.staticred.addon.events;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.util.SyncManager;
import de.staticred.dbv2.DBUtil;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class DiscordMemberLeft extends ListenerAdapter {


    public void onGuildMemberLeave(GuildMemberRemoveEvent event) {
        try {
            if (VerifyAddon.getInstance().getVerifyDAO().isVerified(event.getMember().getIdLong())) {
                UUID linkedPlayer = VerifyAddon.getInstance().getVerifyDAO().getUUID(event.getMember().getIdLong()).get();

                VerifyAddon.getInstance().getVerifyDAO().setDiscordID(linkedPlayer, 0);

                VerifyAddon.getInstance().getVerifyDAO().setVerifyState(linkedPlayer, false);


                if (VerifyAddon.getInstance().getConfigManager().getPrioritizeDiscordRoles())
                    SyncManager.removeAllRoles(event.getMember());
            }
        } catch (SQLException e) {
            DBUtil.getINSTANCE().getLogger().postError("Error while unlinking player while leaving discord");
        }

    }

}
