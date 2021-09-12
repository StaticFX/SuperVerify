package de.staticred.addon.daos;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public interface VerifyDAO {


    /**
     * Checks if the player is verified
     *
     * @param uuid the player
     * @return if verified
     */
    boolean isVerified(UUID uuid) throws SQLException;

    /**
     * Gets discordID from a player.
     *
     *
     * @param uuid the player
     * @return optional DiscordID
     */
    Optional<Long> getDiscordID(UUID uuid) throws SQLException;

    /**
     * sets discordID
     * @param uuid the player
     * @param discordID the id
     */
    void setDiscordID(UUID uuid, long discordID) throws SQLException;

    /**
     * sets the verify state
     * @param uuid the player
     * @param state the state
     */
    void setVerifyState(UUID uuid, boolean state) throws SQLException;

    /**
     * Returns name of the player
     * @param uuid the name
     * @return optional name
     */
    Optional<String> getName(UUID uuid) throws SQLException;

    /**
     * Sets the name of the player
     * @param uuid the player
     * @param name the name
     */
    void setName(UUID uuid, String name) throws SQLException;

    /**
     * Gets UUID from a discord id
     *
     * @param discordID the id
     * @return optional uuid
     */
    Optional<UUID> getUUID(long discordID) throws SQLException;

    /**
     * checks if discordID is linked
     *
     * @param discordID the id
     * @return true if linked
     */
    public boolean isVerified(long discordID) throws SQLException;


    /**
     * Shutsdown the dao
     */
    void shutDown();

}
