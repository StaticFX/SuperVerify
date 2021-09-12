package de.staticred.addon.events;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.util.SyncManager;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.adventure.text.Component;
import de.staticred.dbv2.adventure.text.TextComponent;
import de.staticred.dbv2.adventure.text.event.ClickEvent;
import de.staticred.dbv2.adventure.text.event.HoverEvent;
import de.staticred.dbv2.annotations.EventHandler;
import de.staticred.dbv2.events.JoinEvent;
import de.staticred.dbv2.events.util.Listener;
import de.staticred.dbv2.player.DBUPlayer;

import java.sql.SQLException;


/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class MCJoinEvent implements Listener {

    @EventHandler
    public void onJoin(JoinEvent event) throws SQLException {
        DBUPlayer player = event.getPlayer();

        boolean verified = VerifyAddon.getInstance().getVerifyDAO().isVerified(player.getUUID());

        if (!VerifyAddon.getInstance().getMessageManager().getMinecraftJoin().isEmpty() && !verified) {

            TextComponent component = Component.text(VerifyAddon.getInstance().getMessageManager().getMinecraftJoin().replaceAll("&", "§"));
            component.hoverEvent(HoverEvent.showText(Component.text(VerifyAddon.getInstance().getMessageManager().getHover())));
            component.clickEvent(ClickEvent.openUrl(VerifyAddon.getInstance().getConfigManager().getDiscordURL()));

            player.sendComponent(component);
        }


        if (VerifyAddon.getInstance().getConfigManager().updateUsersOnJoin() && verified) {
            SyncManager.updateRolesAndGroups(player, VerifyAddon.getInstance().getVerifyDAO().getDiscordID(player.getUUID()).get());
        }

    }
}
