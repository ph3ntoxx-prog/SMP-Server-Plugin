package de.simon.legacyCraft.dragon;

import de.simon.legacyCraft.attacks.ShockWave;
import de.simon.legacyCraft.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonListener implements Listener {

    private boolean phase1Started = false;
    private boolean phase2Started = false;
    private boolean phase2Finished = false;
    private final ShockWave shockWave = new ShockWave(Main.getInstance());

    @EventHandler
    public void onDragonDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof EnderDragon dragon)) return;

        // Wenn Phase 1 noch nicht läuft → jetzt starten
        if (!phase1Started) {
            phase1Started = true;
            startPhase1(dragon);
        }

        // Phase 2 starten bei 500 HP oder weniger
        if (!phase2Started && !phase2Finished && dragon.getHealth() <= 500) {
            phase2Started = true;
            startPhase2(dragon);
            e.setCancelled(true);
            return;
        }

        // In Phase 2 kein Schaden
        if (phase2Started) {
            e.setCancelled(true);
        }
    }

    private void startPhase1(EnderDragon dragon) {

        Main.getInstance().getDragonEgg().resetFirstCollected();

        // Drache auf 1000 HP fixen
        dragon.setMaxHealth(1000);
        dragon.setHealth(1000);

        shockWave.startRandomSpecialAttacks(dragon);

        // Regeneration starten (1 HP pro Tick)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.isDead() || phase2Started) {
                    cancel();
                    return;
                }
                double newHealth = Math.min(dragon.getHealth() + 0, dragon.getMaxHealth());
                dragon.setHealth(newHealth);
            }
        }.runTaskTimer(Main.getInstance(), 0L, 0L);

        // Spieler informieren
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendTitle("§aPhase 1 gestartet!", "Der Drache regeneriert langsam...", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        });

        Main.getInstance().getExplosiveAttack().startRandomExplosions();

    }

    private void startPhase2(EnderDragon dragon) {
        Main.getInstance().getWaveManager().startPhase2(dragon);

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendTitle("§cPhase 2 beginnt!", "", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
        });
    }

    public void setPhase2Started(boolean started) {
        this.phase2Started = started;
    }

    public void setPhase2Finished(boolean finished) {
        this.phase2Finished = finished;
    }

}
