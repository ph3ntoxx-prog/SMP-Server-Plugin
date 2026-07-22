package de.simon.legacyCraft.countdown;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CountdownManager {

    private final JavaPlugin plugin;
    private int taskId = -1;

    public CountdownManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startCountdown(String title, long seconds) {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            long time = seconds;

            @Override
            public void run() {

                if (time <= 0) {

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(ChatColor.GREEN +"✔ " + title + " Event startet!"));
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10f, 1f);
                    }

                    Bukkit.getScheduler().cancelTask(taskId);
                    taskId = -1;
                    return;

                }

                String formatted = formatDynamic(time);

                for (Player p: Bukkit.getOnlinePlayers()) {
                    TextComponent message = new TextComponent(
                            ChatColor.WHITE + "" + title + ": " +
                            ChatColor.GOLD + "" + formatted
                    );
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
                }

                time--;
            }
        }, 0L, 20L);
    }

    private String formatDynamic(long seconds) {
        long d = seconds / 86400;
        long remainder = seconds % 86400;
        long h = remainder / 3600;
        remainder %= 3600;
        long m = remainder / 60;
        long s = remainder % 60;

        // Wenn Tage > 0 → ALLES zeigen
        if (d > 0) {
            return d + "d " + h + "h " + m + "m " + s + "s";
        }

        // Wenn Stunden > 0 → Stunden + Minuten + Sekunden
        if (h > 0) {
            return h + "h " + m + "m " + s + "s";
        }

        // Wenn Minuten > 0 → Minuten + Sekunden
        if (m > 0) {
            return m + "m " + s + "s";
        }

        // Sonst nur Sekunden
        return s + "s";
    }
}
