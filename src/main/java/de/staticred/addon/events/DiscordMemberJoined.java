package de.staticred.addon.events;

import de.staticred.addon.VerifyAddon;
import de.staticred.dbv2.discord.util.BotHelper;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class DiscordMemberJoined extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (VerifyAddon.getInstance().getConfigManager().assignRoleOnJoin())
            BotHelper.guild.addRoleToMember(event.getMember(), BotHelper.guild.getRoleById(VerifyAddon.getInstance().getConfigManager().getAssignRoleOnJoin())).queue();

        if (!VerifyAddon.getInstance().getMessageManager().getDiscordJoin().isEmpty())
            event.getMember().getUser().openPrivateChannel().complete().sendMessage(VerifyAddon.getInstance().getMessageManager().getDiscordJoin()).queue();

    }

}
