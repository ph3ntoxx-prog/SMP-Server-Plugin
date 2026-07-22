package de.simon.legacyCraft.dragon;

import de.simon.legacyCraft.Main;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class ProjectileManager {

    private final EnderDragon dragon;

    public ProjectileManager(EnderDragon dragon) {
        this.dragon = dragon;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.isDead() || !Main.getInstance().getWaveManager().isPhase2Active()) {
                    cancel();
                    return;
                }

                List<Player> players = dragon.getWorld().getPlayers();
                if (players.isEmpty()) return;

                for (Player target : players) {
                    // Feuerball
                    Fireball fb = dragon.getWorld().spawn(dragon.getLocation().add(0, 1, 0), Fireball.class);
                    fb.setVelocity(calculateVelocity(target, 2.7));
                    fb.setYield(2); // Explosion etwas stärker

                    // Wind Charge (kleiner Feuerball, kein Schaden)
                    Fireball wc = dragon.getWorld().spawn(dragon.getLocation().add(0, 1, 0), Fireball.class);
                    wc.setVelocity(calculateVelocity(target, 1.2));
                    wc.setYield(0);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 40L); // alle 2 Sekunden
    }

    /**
     * Berechnet den Richtungsvektor für das Projektil auf einen Spieler
     * @param target Spielerziel
     * @param speed Geschwindigkeit
     * @return Richtungsvektor
     */
    private Vector calculateVelocity(Player target, double speed) {
        Vector dir = target.getLocation().toVector().subtract(dragon.getLocation().toVector());
        dir.setY(dir.getY() + 0.5); // leicht nach oben zielen
        return dir.normalize().multiply(speed);
    }
}
