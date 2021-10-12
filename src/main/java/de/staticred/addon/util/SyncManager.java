package de.staticred.addon.util;

import de.staticred.addon.VerifyAddon;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.discord.util.BotHelper;
import de.staticred.dbv2.player.DBUPlayer;
import de.staticred.dbv2.util.Logger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class SyncManager {

    private SyncManager() {
    }

    public static void updateRolesAndGroups(DBUPlayer dbuPlayer, long id) {
        Logger logger = DBUtil.getINSTANCE().getLogger();

        logger.postDebug("Updating user " + dbuPlayer.getName() + " discord: " + id);
        if (VerifyAddon.getInstance().getConfigManager().getPrioritizeDiscordRoles()) {
            logger.postDebug("updating mc groups");
            updateMCRoles(dbuPlayer, id);
        } else {
            logger.postDebug("updating dc groups");
            updateDCRoles(dbuPlayer, id);
        }


    }

    private static void updateDCRoles(DBUPlayer player, long id) {
        Logger logger = DBUtil.getINSTANCE().getLogger();
        Member member = BotHelper.guild.retrieveMemberById(id).complete();

        logger.postDebug("Checking if member is null:");
        if (member == null) {
            logger.postDebug("True, stopping process...");
            player.sendMessage(VerifyAddon.getInstance().getMessageManager().getErrorOccurred());
            return;
        }
        logger.postDebug("False");

        logger.postDebug("Checking if member is owner:");
        if (member.isOwner()) {
            logger.postDebug("True, stopping process...");
            player.sendMessage(VerifyAddon.getInstance().getMessageManager().getErrorOccurred());
            return;
        }
        logger.postDebug("False");

        List<Role> rolesToAdd = new ArrayList<>();

        logger.postDebug("Checking if user has nonDynamicDiscordRole:");
        boolean hasNonDynamic = hasNonDynamicRole(member);
        logger.postDebug(String.valueOf(hasNonDynamic));

        List<Role> rolesToRemove = new ArrayList<>();

        logger.postDebug("Generating groups to remove");
        logger.postDebug("Starting grouploop");
        for (Role role : member.getRoles()) {
            logger.postDebug("-------------");
            logger.postDebug("Current Role: " + role.getName());
            logger.postDebug("Checking if role is registered:");
            if (!VerifyAddon.getInstance().getGroupsFile().roleRegistered(role.getIdLong())) {
                logger.postDebug("False");
                continue;
            }
            logger.postDebug("True");
            logger.postDebug("Getting group for role: " + role.getName());
            Group group = VerifyAddon.getInstance().getGroupsFile().getGroupForID(role.getIdLong());
            logger.postDebug("Group found for role: " + group.getName());

            logger.postDebug("Checking if user has ingame permission for role: ");

            List<Group> groups = getUsersGroups(player);


            if (!player.hasPermission(group.getPermission()) || !checkIfGroupIsHighest(groups, group)) {
                rolesToRemove.add(role);
                logger.postDebug("False, removing group");
            } else {
                logger.postDebug("True");
            }
        }

        logger.postDebug("---------------");
        logger.postDebug("Checking if any role to remove is non dynamic:");
        if (rolesToRemove.stream().anyMatch(role -> !getGroupForRole(VerifyAddon.getInstance().getGroupsFile().getAllGroups(), role.getId()).isDynamic()))
            hasNonDynamic = false;
        logger.postDebug(String.valueOf(hasNonDynamic));

        logger.postDebug("Starting grouploop for roles to add (looping trough groups the user has permission for)");
        for (Group group : getUsersGroups(player)) {
            logger.postDebug("------------");
            logger.postDebug("Current group: " + group.getName());

            logger.postDebug("Checking if group is dynamic:");
            if (group.isDynamic()) {
                logger.postDebug("True");
                logger.postDebug("Getting role for group: ");
                Role role = BotHelper.getRole(group.getDiscordID());
                logger.postDebug(role.getId());
                logger.postDebug("Checking if member has role:");
                if (!member.getRoles().contains(role)) {
                    rolesToAdd.add(role);
                    logger.postDebug("False, adding role");
                } else {
                    logger.postDebug("False");
                }
                continue;
            }
            logger.postDebug("False");
            logger.postDebug("Checking if user hasNonDynamicRole");
            if (!hasNonDynamic) {
                logger.postDebug("False");
                logger.postDebug("Getting role for group: ");
                Role role = BotHelper.getRole(group.getDiscordID());
                logger.postDebug(role.getId());
                logger.postDebug("Checking if member has role:");
                if (!member.getRoles().contains(role)) {
                    rolesToAdd.add(role);
                    logger.postDebug("False, adding role");
                    hasNonDynamic = true;
                    logger.postDebug("Checking if to sync userNames:");
                    if (VerifyAddon.getInstance().getConfigManager().getSyncUserNames()) {
                        syncNameMCToDC(group, player, id);
                        logger.postDebug("True");
                    } else {
                        logger.postDebug("False");
                    }
                } else {
                    logger.postDebug("False");
                }

                continue;
            }
            logger.postDebug("True");
        }
        logger.postDebug("-----------");
        logger.postDebug("Finished updating");



        rolesToAdd.forEach(role -> member.getGuild().addRoleToMember(member, role).queue());
        rolesToRemove.forEach(role -> member.getGuild().removeRoleFromMember(member, role).queue());


    }

    private static boolean checkIfGroupIsHighest(List<Group> groups, Group group) {
        return groups.indexOf(group) == 0;
    }

    private static LinkedList<Group> getUsersGroups(DBUPlayer player) {
        LinkedList<Group> groups = new LinkedList<>();

        boolean hasNonDynamic = false;
        for (Group group : VerifyAddon.getInstance().getGroupsFile().getAllGroups()) {
            if (group.isDynamic() && player.hasPermission(group.getPermission()))
                groups.add(group);
            else {
                if (hasNonDynamic)
                    continue;
                if (player.hasPermission(group.getPermission())) {
                    groups.add(group);
                    hasNonDynamic = true;
                }
            }
        }

        return groups;
    }

    private static void updateMCRoles(DBUPlayer dbuPlayer, long id) {
        Member member = BotHelper.guild.retrieveMemberById(id).complete();
        Logger logger = DBUtil.getINSTANCE().getLogger();

        logger.postDebug("Checking if member is null:");
        if (member == null) {
            logger.postDebug("True, stopping process..");
            dbuPlayer.sendMessage(VerifyAddon.getInstance().getMessageManager().getErrorOccurred());
            return;
        }
        logger.postDebug("False");

        ArrayList<Group> rolesToAdd = new ArrayList<>();

        logger.postDebug("Checking if user has already non dynamic group.");
        boolean hasNonDynamicRole = hasNonDynamicGroup(dbuPlayer);
        logger.postDebug(String.valueOf(hasNonDynamicRole));

        ArrayList<Group> rolesToRemove = new ArrayList<>();

        logger.postDebug("Checking what groups to remove.");
        logger.postDebug("Starting grouploop");
        for (Group group : getUsersGroups(dbuPlayer)) {
            logger.postDebug("---------------");
            logger.postDebug("Current group: " + group.getName());
            logger.postDebug("Checking if user has role on discord: " + group.getDiscordID());
            if (member.getRoles().contains(BotHelper.getRole(group.getDiscordID()))) {
                logger.postDebug("True");
                continue;
            }
            logger.postDebug("False");

            logger.postDebug("Checking if role is the highest the user has or if he has higher group:");
            if (!checkIfRoleIsHighest(getRegisterRoles(), BotHelper.getRole(group.getDiscordID()))) {
                rolesToRemove.add(group);
                logger.postDebug("False, group will be removed.");
            }
            logger.postDebug("True");
        }

        logger.postDebug("------------");
        logger.postDebug("Check if groups to remove contains nonDynamic:");
        if (rolesToRemove.stream().anyMatch(group -> !group.isDynamic())) {
            hasNonDynamicRole = false;
            logger.postDebug("True");
        } else {
            logger.postDebug("False");
        }

        logger.postDebug("Checking what roles to add");
        logger.postDebug("Starting grouploop...");
        for (Role role : member.getRoles()) {

            if (!VerifyAddon.getInstance().getGroupsFile().roleRegistered(role.getIdLong()))
                continue;

            Group group = getGroupForRole(VerifyAddon.getInstance().getGroupsFile().getAllGroups(), role.getId());
            logger.postDebug("--------------");
            logger.postDebug("Current role: " + role.getName());
            logger.postDebug("Checking if group is dynamic:");
            if (group.isDynamic()) {
                logger.postDebug("True");
                logger.postDebug("Checking if user has permission: " + group.getPermission());
                if (dbuPlayer.hasPermission(group.getPermission())) {
                    rolesToAdd.add(group);
                    logger.postDebug("True");
                    continue;
                }
                logger.postDebug("False");
                continue;
            }
            logger.postDebug("False");

            logger.postDebug("Checking if user has group already");
            if (getUsersGroups(dbuPlayer).stream().anyMatch(group1 -> group1.getName().equals(group.getName()))) {
                logger.postDebug("True");
                continue;
            }
            logger.postDebug("False");
            logger.postDebug("Checking if user hasNonDynamicGroup");
            if (!hasNonDynamicRole) {
                logger.postDebug("False");
                rolesToAdd.add(group);
                hasNonDynamicRole = true;
                logger.postDebug("Checking if syncNicknames:");
                if (VerifyAddon.getInstance().getConfigManager().getSyncUserNames()) {
                    logger.postDebug("True");
                    syncNameMCToDC(group, dbuPlayer, id);
                } else {
                    logger.postDebug("False");
                }
                continue;

            }
            logger.postDebug("True");
        }
        logger.postDebug("------------");

        logger.postDebug("Amount of roles to remove: " + rolesToRemove.size());
        logger.postDebug("Amount of roles to add: " + rolesToAdd.size());

        rolesToAdd.forEach(group -> addMCGroup(dbuPlayer, group.getMcGroup()));
        rolesToRemove.forEach(group -> removeMCGroup(dbuPlayer, group.getMcGroup()));
    }

    private static boolean checkIfRoleIsHighest(List<Role> roles, Role role) {
        return roles.stream().noneMatch(role1 -> role1.getPosition() >= role.getPosition());
    }

    private static boolean hasNonDynamicRole(Member member) {
        for (Role role : member.getRoles()) {
            if (VerifyAddon.getInstance().getGroupsFile().roleRegistered(role.getIdLong())) {
                Group group = VerifyAddon.getInstance().getGroupsFile().getGroupForID(role.getIdLong());
                if (!group.isDynamic())
                    return true;
            }
        }

        return false;
    }

    private static boolean hasNonDynamicGroup(DBUPlayer player) {
        if (!VerifyAddon.getInstance().getConfigManager().getPrioritizeDiscordRoles())
            return getUsersGroups(player).stream().anyMatch(group -> !group.isDynamic());
        return false;
    }

    public static void unlink(DBUPlayer player, long discordid) {
        List<Group> groups = getUsersGroups(player);

        Member member = BotHelper.guild.retrieveMemberById(discordid).complete();

        if (member == null) {
            player.sendMessage(VerifyAddon.getInstance().getMessageManager().getErrorOccurred());
            return;
        }

        member.modifyNickname(member.getUser().getName()).queue();

        if (member.isOwner()) {
            player.sendMessage(VerifyAddon.getInstance().getMessageManager().getErrorOccurred());
            return;
        }


        if (VerifyAddon.getInstance().getConfigManager().getPrioritizeDiscordRoles()) {
            groups.forEach(group -> removeMCGroup(player, group.getMcGroup()));
        } else {
            for (Role role : member.getRoles()) {
                if (!VerifyAddon.getInstance().getGroupsFile().roleRegistered(role.getIdLong()))
                    continue;

                member.getGuild().removeRoleFromMember(member, role).queue();
            }
        }
    }


    public static void removeAllRoles(Member member) {
        for (Group group : VerifyAddon.getInstance().getGroupsFile().getAllGroups()) {
            Role role = BotHelper.getRole(group.getDiscordID());
            member.getGuild().removeRoleFromMember(member, role).queue();
        }

        member.getGuild().modifyNickname(member, member.getEffectiveName()).queue();

    }

    public static void syncNameMCToDC(Group group, DBUPlayer player, long discordid) {
        BotHelper.guild.modifyNickname(BotHelper.guild.retrieveMemberById(discordid).complete(), group.getNickname().replaceAll("%player%", player.getName())).queue();
    }


    private static void addMCGroup(DBUPlayer player, String group) {
        VerifyAddon.getInstance().getProxy().executeConsoleCommand(VerifyAddon.getInstance().getConfigManager().getAddGroupCommand().replaceAll("%name%", player.getName()).replaceAll("%role%", group));
    }

    private static void removeMCGroup(DBUPlayer player, String group) {
        VerifyAddon.getInstance().getProxy().executeConsoleCommand(VerifyAddon.getInstance().getConfigManager().getRemoveGroupCommand().replaceAll("%name%", player.getName()).replaceAll("%role%", group));
    }

    private static Group getGroupForRole(List<Group> groups, String id) {
        return groups.stream().filter(group -> group.getDiscordID().equals(id)).findFirst().orElse(null);
    }


    private static List<Role> getRegisterRoles() {
        List<Role> roles = new ArrayList<>();
        for(Group group : VerifyAddon.getInstance().getGroupsFile().getAllGroups()) {
            roles.add(BotHelper.getRole(group.getDiscordID()));
        }
        return roles;
    }


}
