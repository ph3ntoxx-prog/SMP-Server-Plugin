package de.simon.legacyCraft.leash;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeashManager {

    private static final Map<UUID, UUID> leashed = new HashMap<>();

    public static void leash(Player holder, Player target) {
        leashed.put(target.getUniqueId(), holder.getUniqueId());
        target.sendMessage("§cDu wurdest angeleint!");
        holder.sendMessage("§aDu hast " + target.getName() + " angeleint.");
    }

    public static void unleash(Player target) {
        leashed.remove(target.getUniqueId());
        target.sendMessage("§aDu wurdest losgeleint.");
    }

    public static boolean isLeashed(Player target) {
        return leashed.containsKey(target.getUniqueId());
    }

    public static UUID getHolder(Player target) {
        return leashed.get(target.getUniqueId());
    }
}
