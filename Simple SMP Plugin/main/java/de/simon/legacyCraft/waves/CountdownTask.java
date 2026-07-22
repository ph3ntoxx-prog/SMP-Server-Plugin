package de.simon.legacyCraft.waves;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTask extends BukkitRunnable {

    private int seconds;
    private final Runnable nextWave;

    public CountdownTask(int seconds, Runnable nextWave) {
        this.seconds = seconds;
        this.nextWave = nextWave;
    }

    @Override
    public void run() {
        if (seconds <= 0) {
            cancel();
            nextWave.run();
            return;
        }

        // ActionBar Text (jede Sekunde ok)
        String text = "§l§eNächste Welle in: " + seconds + "s";

        boolean playSound =
                seconds <= 5 ||          // Countdown-Ende
                        seconds == 10 ||
                        seconds == 30;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendActionBar(net.kyori.adventure.text.Component.text(text));

            // 🔥 Sound NUR bei wichtigen Sekunden
            if (playSound) {
                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_COPPER_BULB_TURN_OFF,
                        0.8f,
                        1f
                );
            }
        }

        seconds--;
    }
}