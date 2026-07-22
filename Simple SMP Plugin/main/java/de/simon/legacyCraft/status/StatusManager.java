package de.simon.legacyCraft.status;

import java.util.HashMap;
import java.util.Map;

public class StatusManager {

    private final Map<String, String[]> statuses = new HashMap<>();

    public String[] getPlayerStatus(String playerName) {
        return statuses.getOrDefault(playerName, new String[]{"", "WHITE"});
    }

    public void setPlayerStatus(String playerName, String statusName, String colorName) {
        statuses.put(playerName, new String[]{statusName, colorName});
    }

    public void removePlayerStatus(String playerName) {
        statuses.remove(playerName);
    }
}
