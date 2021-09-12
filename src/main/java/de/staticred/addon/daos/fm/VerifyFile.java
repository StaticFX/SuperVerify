package de.staticred.addon.daos.fm;

import de.staticred.addon.VerifyAddon;
import de.staticred.addon.daos.VerifyDAO;
import de.staticred.dbv2.DBUtil;
import de.staticred.dbv2.files.util.DBUtilFile;
import de.staticred.dbv2.player.DBUPlayer;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Devin Fritz
 * @version 1.0.0
 */
public class VerifyFile extends DBUtilFile implements VerifyDAO {



    public VerifyFile(File current) {
        super(current, "addonfiles/verify.yml");
    }

    private boolean isInFile(UUID uuid) {
        return getConfiguration().contains(uuid.toString());
    }

    private void addToFile(UUID uuid) {
        if (isInFile(uuid))
            return;

        DBUPlayer player = DBUtil.getINSTANCE().getProxy().getPlayer(uuid);

        if (player == null) {
            DBUtil.getINSTANCE().getErrorLogger().postError("Error while trying to add player to verify.yml");
            return;
        }

        String uuidS = uuid.toString();

        getConfiguration().set(uuidS + ".name", player.getName());
        getConfiguration().set(uuidS + ".discordID", "N/A");
        getConfiguration().set(uuidS + ".verified", false);

        saveData();
    }

    @Override
    public boolean isVerified(UUID uuid) {
        addToFile(uuid);
        return getConfiguration().getBoolean(uuid.toString() + ".verified");
    }

    @Override
    public boolean isVerified(long discordID) {
        return getUUID(discordID).isPresent();
    }

    @Override
    public Optional<Long> getDiscordID(UUID uuid) {
        addToFile(uuid);
        String id = getConfiguration().getString(uuid.toString() + ".discordID");

        if (id.equals("N/A"))
            return Optional.empty();

        long idLong = Long.parseLong(id);

        return Optional.of(idLong);
    }

    @Override
    public void setDiscordID(UUID uuid, long discordID) {
        addToFile(uuid);
        getConfiguration().set(uuid.toString() + ".discordID", discordID);
        saveData();
    }

    @Override
    public void setVerifyState(UUID uuid, boolean state) {
        addToFile(uuid);
        getConfiguration().set(uuid.toString() + ".verified", state);
        saveData();
    }

    @Override
    public Optional<String> getName(UUID uuid) {
        if (!getConfiguration().contains(uuid.toString() + ".name"))
            return Optional.empty();

        return Optional.of(getConfiguration().getString(uuid.toString() + ".name"));
    }

    @Override
    public void setName(UUID uuid, String name) {
        getConfiguration().set(uuid.toString() + ".name", name);
        saveData();
    }

    @Override
    public Optional<UUID> getUUID(long discordID) {
        if (!containsDiscordID(discordID)) {
            return Optional.empty();
        }

        for (String key : getConfiguration().getKeys(false)) {

            UUID uuid = UUID.fromString(key);

            Optional<Long> discordIDOptional = getDiscordID(uuid);

            if (discordIDOptional.isPresent()) {
                long dID = discordIDOptional.get();

                if (dID == discordID)
                    return Optional.of(uuid);

            }
        }
        return Optional.empty();
    }


    private boolean containsDiscordID(long discordID) {
        for (String key : getConfiguration().getKeys(false)) {

            UUID uuid = UUID.fromString(key);

            Optional<Long> discordIDOptional = getDiscordID(uuid);

            if (discordIDOptional.isPresent()) {
                if (discordIDOptional.get() == discordID)
                    return true;
            }
        }
        return false;
    }


    @Override
    public void shutDown() {
        saveData();
    }

    @Override
    public void afterLoad() {
    }
}
