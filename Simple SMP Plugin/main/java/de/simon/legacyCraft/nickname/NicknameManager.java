package de.simon.legacyCraft.nickname;

import java.util.HashMap;
import java.util.UUID;

public class NicknameManager {

    private static final HashMap<UUID, String> nicknames = new HashMap<>();

    public static void set(UUID uuid, String nickname) {
        nicknames.put(uuid, nickname);
    }

    public static void remove(UUID uuid) {
        nicknames.remove(uuid);
    }

    public static String get(UUID uuid, String fallback) {
        return nicknames.getOrDefault(uuid, fallback);
    }

    public static boolean has(UUID uuid) {
        return nicknames.containsKey(uuid);
    }
}
