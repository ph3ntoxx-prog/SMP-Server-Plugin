package de.simon.legacyCraft.ban;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class BanManager {

    private static File file;
    private static FileConfiguration cfg;

    public static void init(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "bans.yml");

        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public static void ban(Player target, long start, long end, String reason) {
        String path = "bans." + target.getUniqueId();

        cfg.set(path + ".name", target.getName());
        cfg.set(path + ".start", start);
        cfg.set(path + ".end", end);
        cfg.set(path + ".reason", reason);

        save();

        if (System.currentTimeMillis() >= start) {
            target.kickPlayer(buildKickMessage(start, end, reason));
        }
    }

    public static boolean isBanned(UUID uuid) {
        String path = "bans." + uuid;
        if (!cfg.contains(path)) return false;

        long now = System.currentTimeMillis();
        long start = cfg.getLong(path + ".start");
        long end = cfg.getLong(path + ".end");

        if (now > end) {
            cfg.set(path, null);
            save();
            return false;
        }

        return now >= start;
    }

    public static long getStart(UUID uuid) {
        return cfg.getLong("bans." + uuid + ".start");
    }

    public static long getEnd(UUID uuid) {
        return cfg.getLong("bans." + uuid + ".end");
    }

    public static String getReason(UUID uuid) {
        return cfg.getString("bans." + uuid + ".reason", "Kein Grund");
    }

    private static void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildKickMessage(long start, long end, String reason) {
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        f.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));

        return "§cDu bist gebannt!\n\n" +
                "§7Von: §c" + f.format(new Date(start)) + "\n" +
                "§7Bis: §c" + f.format(new Date(end)) + "\n" +
                "§7Grund: §c" + reason;
    }

    public static String getRemainingTime(UUID uuid) {
        long end = getEnd(uuid);
        long diff = end - System.currentTimeMillis();

        if (diff <= 0) return "Abgelaufen";

        long minutes = diff / 1000 / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        hours %= 24;
        minutes %= 60;

        if (days > 0)
            return days + " Tage " + hours + " Stunden";
        if (hours > 0)
            return hours + " Stunden " + minutes + " Minuten";

        return minutes + " Minuten";
    }

    public static void unban(UUID uuid) {
        String path = "bans." + uuid;
        if (cfg.contains(path)) {
            cfg.set(path, null);
            save();
        }
    }

}
