package de.simon.legacyCraft.protect;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LogManager {

    private final JavaPlugin plugin;
    private final File logFile;
    private final FileConfiguration logConfig;

    // 🔥 Cache + Save-Queue
    private final Set<String> dirtyKeys = new HashSet<>();
    private boolean saveScheduled = false;

    // 🔥 Formatter EINMAL erstellen
    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public LogManager(JavaPlugin plugin) {
        this.plugin = plugin;
        logFile = new File(plugin.getDataFolder(), "blocklogs.yml");

        if (!logFile.exists()) {
            logFile.getParentFile().mkdirs();
            plugin.saveResource("blocklogs.yml", false);
        }

        logConfig = YamlConfiguration.loadConfiguration(logFile);
    }

    /**
     * 🔥 NICHT MEHR SYNC SPEICHERN
     */
    public void addLog(Block block, String playerName, String action) {
        String key = locationKey(block.getLocation());
        List<String> logs = logConfig.getStringList(key);
        logs.add(formatLog(playerName, action));
        logConfig.set(key, logs);

        dirtyKeys.add(key);
        scheduleSave();
    }

    /**
     * 🔥 Batch-Save (alle 5 Sekunden max.)
     */
    private void scheduleSave() {
        if (saveScheduled) return;

        saveScheduled = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                saveScheduled = false;

                if (dirtyKeys.isEmpty()) return;

                dirtyKeys.clear();

                try {
                    logConfig.save(logFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLaterAsynchronously(plugin, 100L); // 5 Sekunden
    }

    private String formatLog(String player, String action) {
        ZonedDateTime now = ZonedDateTime.now(ZONE);

        String date = ChatColor.YELLOW + "" + ChatColor.BOLD + now.format(DATE_FORMAT);
        String time = ChatColor.AQUA + "" + ChatColor.BOLD + now.format(TIME_FORMAT);
        String name = ChatColor.GREEN + "" + ChatColor.BOLD + player;
        String act = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + action;

        return date + " | " + time + " | " + name + " | " + act;
    }

    private String locationKey(Location loc) {
        return loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }

    /**
     * Lesen bleibt SYNC (Command)
     */
    public boolean showBlockLogs(Player player) {

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung diesen Command auszuführen!");
            return true;
        }

        Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Du musst auf einen Block schauen!");
            return true;
        }

        List<String> logs = logConfig.getStringList(locationKey(target.getLocation()));

        if (logs.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Keine Daten für diesen Block.");
        } else {
            player.sendMessage(ChatColor.GOLD + "Letzte Aktionen an diesem Block:");
            for (String log : logs) {
                player.sendMessage(log);
            }
        }
        return true;
    }
}
