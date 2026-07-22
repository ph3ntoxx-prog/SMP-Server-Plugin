package de.simon.legacyCraft.waves;

import de.simon.legacyCraft.attacks.ExplosiveAttack;
import de.simon.legacyCraft.attacks.ShockWave;
import de.simon.legacyCraft.bosses.EndermiteBoss;
import de.simon.legacyCraft.dragon.ProjectileManager;
import de.simon.legacyCraft.Main;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WaveManager {

    private EnderDragon dragon;
    private int currentWave = 0;
    private boolean phase2Active = false;
    private boolean phase1Active = false;

    private final Random random = new Random();
    private ShockWave shockWave;
    private ExplosiveAttack explosiveAttack;
    private EndermiteBoss endermiteBoss;
    private Location endCenter;

    // 🔧 ZENTRALE MOB-LISTE (Lag-Fix)
    private final Set<LivingEntity> waveMobs = new HashSet<>();

    public void start() {

        this.shockWave = new ShockWave(Main.getInstance());
        phase1Active = true;

        EnderDragon foundDragon = null;
        for (World world : dragonWorlds()) {
            for (Entity e : world.getEntities()) {
                if (e instanceof EnderDragon) {
                    foundDragon = (EnderDragon) e;
                    break;
                }
            }
            if (foundDragon != null) break;
        }

        if (foundDragon == null) {
            Main.getInstance().getLogger().warning("Kein EnderDragon gefunden!");
            return;
        }

        this.dragon = foundDragon;

        dragon.setMaxHealth(1000);
        dragon.setHealth(1000);

        shockWave.startRandomSpecialAttacks(dragon);
        startMobParticleTask(); // 🔥 EIN Task statt hunderte
    }

    public void startPhase2(EnderDragon dragon) {

        phase1Active = false;

        this.endermiteBoss = new EndermiteBoss(Main.getInstance());
        this.endCenter = new Location(dragon.getWorld(), 0, 79, 0);
        this.dragon = dragon;
        phase2Active = true;
        dragon.setInvulnerable(true);

        for (Player p : dragon.getWorld().getPlayers()) {
            p.sendTitle("§l§cPhase 2 beginnt!", "", 10, 70, 20);
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
        }

        new ProjectileManager(dragon).start();

        startWave1();
        startPhase2Particles(dragon);
    }

    private void startWave1() {
        currentWave = 1;
        announceWave("", Sound.ENTITY_WITHER_SPAWN);
        spawnSkeletons(100);
        spawnLightning(20);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.getWorld().getEntitiesByClass(Skeleton.class).isEmpty()) {
                    completeWave("Welle 1 abgeschlossen!");
                    startCountdown(150, WaveManager.this::startWave2);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 40L);
    }

    private void startWave2() {
        currentWave = 2;
        announceWave("", Sound.ENTITY_WITHER_SPAWN);
        spawnPillagers(40);
        spawnVindicators(40);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.getWorld().getEntitiesByClasses(Pillager.class, Vindicator.class).isEmpty()) {
                    completeWave("Welle 2 abgeschlossen!");
                    startCountdown(150, WaveManager.this::startWave3);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 40L);
    }

    private void startWave3() {
        currentWave = 3;
        announceWave("", Sound.ENTITY_WITHER_SPAWN);
        spawnGhasts(20);
        spawnWither(3);
        spawnMagmaCubes(20);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.getWorld().getEntitiesByClasses(Ghast.class, Wither.class, MagmaCube.class).isEmpty()) {
                    completeWave("Alle Wellen abgeschlossen!");
                    endPhase2();
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 40L);
    }

    private void endPhase2() {
        new BukkitRunnable() {
            @Override
            public void run() {
                dragon.setInvulnerable(false);
                phase2Active = false;

                startPhase3Particles(dragon);

                for (Player p : dragon.getWorld().getPlayers()) {
                    p.sendTitle("§l§aPhase 2 beendet!", "", 10, 70, 20);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                }
            }
        }.runTaskLater(Main.getInstance(), 40L);

        Main.getInstance().getDragonListener().setPhase2Started(false);
        Main.getInstance().getDragonListener().setPhase2Finished(true);
    }

    private void startCountdown(int seconds, Runnable nextWave) {
        new CountdownTask(seconds, nextWave).runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private void announceWave(String message, Sound sound) {
        for (Player p : dragon.getWorld().getPlayers()) {
            p.sendTitle("§e" + message, "", 10, 40, 10);
            p.playSound(p.getLocation(), sound, 2f, 1f);
        }
    }

    private void completeWave(String message) {
        for (Player p : dragon.getWorld().getPlayers()) {
            p.sendTitle("§a" + message, "", 10, 50, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2f, 1f);
        }

        if (endermiteBoss != null && !dragon.isDead()) {
            endermiteBoss.spawnTwoRandom(endCenter);
        }
    }

    // ---------------- SPAWNS ----------------

    private void spawnSkeletons(int count) {
        for (int i = 0; i < count; i++) registerMob(
                dragon.getWorld().spawn(randomLocation(), Skeleton.class)
        );
    }

    private void spawnPillagers(int count) {
        for (int i = 0; i < count; i++) registerMob(
                dragon.getWorld().spawn(randomLocation(), Pillager.class)
        );
    }

    private void spawnVindicators(int count) {
        for (int i = 0; i < count; i++) registerMob(
                dragon.getWorld().spawn(randomLocation(), Vindicator.class)
        );
    }

    private void spawnGhasts(int count) {
        for (int i = 0; i < count; i++) {
            Ghast g = dragon.getWorld().spawn(randomLocation(), Ghast.class);
            g.setSilent(true);
            registerMob(g);
        }
    }

    private void spawnWither(int count) {
        for (int i = 0; i < count; i++) registerMob(
                dragon.getWorld().spawn(randomLocation(), Wither.class)
        );
    }

    private void spawnMagmaCubes(int groups) {
        for (int g = 0; g < groups; g++) {
            Location base = randomLocation();
            for (int i = 0; i < 4; i++) {
                registerMob(
                        dragon.getWorld().spawn(base.clone().add(i, 0, 0), MagmaCube.class)
                );
            }
        }
    }

    private void spawnLightning(int count) {
        for (int i = 0; i < count; i++) {
            dragon.getWorld().strikeLightningEffect(randomLocation());
        }
    }

    private void registerMob(LivingEntity mob) {
        mob.getPersistentDataContainer().set(
                new NamespacedKey(Main.getInstance(), "wave_mob"),
                PersistentDataType.STRING,
                "bittenichtdespawnen"
        );
        mob.setRemoveWhenFarAway(false);
        waveMobs.add(mob);
    }

    private Location randomLocation() {
        World world = dragon.getWorld();
        int x = random.nextInt(100) - 50;
        int z = random.nextInt(100) - 50;
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x + 0.5, y + 1, z + 0.5);
    }

    public boolean isPhase2Active() {
        return phase2Active;
    }

    private World[] dragonWorlds() {
        return Main.getInstance().getServer().getWorlds().toArray(new World[0]);
    }

    // 🔥 EIN globaler Task statt 100+
    private void startMobParticleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                waveMobs.removeIf(m -> m.isDead() || !m.isValid());
                for (LivingEntity mob : waveMobs) {
                    mob.getWorld().spawnParticle(
                            Particle.FLAME,
                            mob.getLocation().add(0, 1, 0),
                            3, 0.3, 0.4, 0.3, 0
                    );
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private void startPhase2Particles(EnderDragon dragon) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.isDead() || !phase2Active) {
                    cancel();
                    return;
                }
                dragon.getWorld().spawnParticle(
                        Particle.FLAME,
                        dragon.getLocation().add(0, 2, 0),
                        20, 1.5, 1, 1.5, 0.1
                );
            }
        }.runTaskTimer(Main.getInstance(), 0L, 5L);
    }

    private void startPhase3Particles(EnderDragon dragon) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.isDead() || phase2Active) {
                    cancel();
                    return;
                }
                dragon.getWorld().spawnParticle(
                        Particle.WITCH,
                        dragon.getLocation().add(0, 2, 0),
                        25, 1.5, 1, 1.5, 0.1
                );
            }
        }.runTaskTimer(Main.getInstance(), 0L, 5L);
    }
}
