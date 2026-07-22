package de.simon.legacyCraft.tpsbar;

import de.simon.legacyCraft.Main;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;

public class TPSBarManager {

    private final Main plugin;
    private final BossBar bossBar;
    private BukkitTask updater;
    private final DecimalFormat df = new DecimalFormat("0.00");

    // 🔹 gecachte Werte
    private volatile double tps = 20.0;
    private volatile double mspt = 0.0;
    private volatile int avgPing = 0;
    private volatile int playerCount = 0;

    public TPSBarManager(Main plugin) {
        this.plugin = plugin;
        this.bossBar = Bukkit.createBossBar(
                "TPS: -- | MSPT: -- | Ping: --ms",
                BarColor.GREEN,
                BarStyle.SOLID
        );
    }

    /** Startet die wiederkehrende Aktualisierung der BossBar. */
    public void startUpdating() {

        // 🔥 Async: Werte berechnen (kein Bukkit-Zugriff außer getTPS)
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Server server = plugin.getServer();

            try {
                double[] tpsArr = server.getTPS();
                if (tpsArr != null && tpsArr.length > 0) tps = tpsArr[0];
            } catch (Throwable ignored) {}

            try {
                mspt = server.getAverageTickTime();
            } catch (Throwable ignored) {}

            int pingSum = 0;
            int count = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    pingSum += p.getPing();
                    count++;
                } catch (Throwable ignored) {}
            }

            playerCount = count;
            avgPing = (count > 0) ? Math.round((float) pingSum / count) : 0;

        }, 0L, 40L); // ⬅️ nur alle 2 Sekunden neu berechnen


        // 🔹 Sync: BossBar anzeigen (leicht)
        updater = Bukkit.getScheduler().runTaskTimer(plugin, this::updateBar, 0L, 20L);
    }

    /** Stoppt die Aktualisierung der BossBar. */
    public void stopUpdating() {
        if (updater != null) updater.cancel();
    }

    /** Fügt einen Spieler zur BossBar hinzu. */
    public void addPlayer(Player player) {
        if (!bossBar.getPlayers().contains(player)) {
            bossBar.addPlayer(player);
        }
    }

    /** Entfernt einen Spieler aus der BossBar. */
    public void removePlayer(Player player) {
        bossBar.removePlayer(player);
    }

    /** Entfernt alle Spieler aus der BossBar. */
    public void removeAllPlayers() {
        bossBar.removeAll();
    }

    /** Prüft, ob die BossBar für den Spieler sichtbar ist. */
    public boolean isVisible(Player player) {
        return bossBar.getPlayers().contains(player);
    }

    /** Aktualisiert Titel, Farbe und Progress der BossBar. */
    private void updateBar() {

        String tpsColor = (tps >= 18) ? "§a" : (tps >= 12 ? "§e" : "§c");
        String msptColor = (mspt <= 10) ? "§a" : (mspt <= 20 ? "§e" : "§c");
        String pingColor = (playerCount == 0 ? "§7"
                : (avgPing <= 100 ? "§a" : (avgPing <= 200 ? "§e" : "§c")));

        String title = String.format(
                "§7TPS: %s%s §7| MSPT: %s%s §7| Ping: %s%s",
                tpsColor, df.format(tps),
                msptColor, df.format(mspt),
                pingColor, (playerCount == 0 ? "-" : avgPing)
        );

        bossBar.setTitle(title);
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, tps / 20.0)));

        bossBar.setColor(
                (tps >= 18.0) ? BarColor.GREEN
                        : (tps >= 12.0 ? BarColor.YELLOW : BarColor.RED)
        );
    }
}