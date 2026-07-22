package de.simon.legacyCraft.vanish;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private static final Set<UUID> vanished = new HashSet<>();

    public static boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public static void setVanished(Player player, boolean value) {
        if (value) {
            vanished.add(player.getUniqueId());
        } else {
            vanished.remove(player.getUniqueId());
        }
    }
}

