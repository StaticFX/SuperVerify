package de.staticred.addon.util;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class Group {

    private final String name;
    private final String discordID;
    private final String mcGroup;
    private final String permission;
    private final String nickname;
    private final boolean dynamic;

    public Group(String name, String discordID, String mcGroup, String permission, String nickname, boolean dynamic) {
        this.name = name;
        this.discordID = discordID;
        this.mcGroup = mcGroup;
        this.permission = permission;
        this.nickname = nickname;
        this.dynamic = dynamic;
    }

    public String getName() {
        return name;
    }

    public String getDiscordID() {
        return discordID;
    }

    public String getMcGroup() {
        return mcGroup;
    }

    public String getPermission() {
        return permission;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isDynamic() {
        return dynamic;
    }
}
