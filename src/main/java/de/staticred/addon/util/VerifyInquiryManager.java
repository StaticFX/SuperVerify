package de.staticred.addon.util;

import de.staticred.addon.VerifyAddon;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.player.DBUPlayer;
import de.staticred.dbv2.util.DoubleOptional;
import de.staticred.dbv2.util.tuple.Triple;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.util.HashMap;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class VerifyInquiryManager {

    private final HashMap<Long, Pair<DoubleOptional<Message, InteractionHook>, DBUPlayer>> playerDiscordIDInquiryMap;


    public VerifyInquiryManager() {
        playerDiscordIDInquiryMap = new HashMap<>();
    }

    public boolean hasSendInquiry(long discordID) {
        return playerDiscordIDInquiryMap.containsKey(discordID);
    }

    public void removeFromInquiryList(long discordID) {
        playerDiscordIDInquiryMap.remove(discordID);
    }

    public DBUPlayer getPlayer(long discordID) {
        return playerDiscordIDInquiryMap.get(discordID).getRight();
    }

    public boolean someoneSendToPlayer(DBUPlayer player) {
        return playerDiscordIDInquiryMap.values().stream().anyMatch(dbuPlayerMessagePair -> dbuPlayerMessagePair.getRight().getUUID().equals(player.getUUID()));
    }

    public void queueInquiry(long discordID, DBUPlayer player, DoubleOptional<Message, InteractionHook> message) {
        playerDiscordIDInquiryMap.put(discordID, new ImmutablePair<>(message, player));
    }

    public long getID(DBUPlayer player) {
        for (long id : playerDiscordIDInquiryMap.keySet()) {

            Pair<DoubleOptional<Message, InteractionHook>, DBUPlayer> p = playerDiscordIDInquiryMap.get(id);

            if (p.getRight().getUUID().equals(player.getUUID()))
                return id;

        }
        return -1;
    }

    public DoubleOptional<Message, InteractionHook> getDoubleOptional(long discordID) {
        return playerDiscordIDInquiryMap.get(discordID).getLeft();
    }

}
