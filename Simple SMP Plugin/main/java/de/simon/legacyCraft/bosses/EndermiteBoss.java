package de.simon.legacyCraft.bosses;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class EndermiteBoss implements Listener {

    private final JavaPlugin plugin;
    private final Random random = new Random();
    private EnderDragon cachedDragon; // 🔧 Cache statt Suche

    public EndermiteBoss(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Spawnt den Endermite Boss an einer Position
     */
    public void spawnBoss(Location loc) {
        World world = loc.getWorld();
        if (world == null) return;

        Endermite boss = world.spawn(loc, Endermite.class, em -> {
            em.setCustomName("§5Endermite");
            em.setCustomNameVisible(true);

            if (em.getAttribute(Attribute.SCALE) != null) {
                em.getAttribute(Attribute.SCALE).setBaseValue(15.0);
            }

            em.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100);
            em.setHealth(100);

            em.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(100);
            em.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.4);

            em.setRemoveWhenFarAway(false);
            em.setPersistent(true);
        });

        EnderDragon dragon = getDragon(world);
        if (dragon != null) {
            createBeam(boss, dragon);
        }
    }

    // 🔧 Dragon nur einmal suchen
    private EnderDragon getDragon(World world) {
        if (cachedDragon != null && cachedDragon.isValid() && !cachedDragon.isDead()) {
            return cachedDragon;
        }

        for (Entity ent : world.getEntitiesByClass(EnderDragon.class)) {
            cachedDragon = (EnderDragon) ent;
            return cachedDragon;
        }
        return null;
    }

    private void createBeam(Endermite boss, EnderDragon dragon) {
        World world = boss.getWorld();

        EnderCrystal crystal = world.spawn(
                boss.getLocation().add(0, 1.5, 0),
                EnderCrystal.class,
                c -> {
                    c.setShowingBottom(false);
                    c.setInvulnerable(true);
                    c.setSilent(true);
                    c.setCustomName("BossBeamCrystal");
                    c.setCustomNameVisible(false);
                }
        );

        crystal.setBeamTarget(dragon.getLocation().add(0, 5, 0));

        // 🔧 nur noch alle 2 Ticks statt jeden Tick
        new BukkitRunnable() {
            @Override
            public void run() {
                if (boss.isDead() || dragon.isDead() || !crystal.isValid()) {
                    crystal.remove();
                    cancel();
                    return;
                }

                Location bossLoc = boss.getLocation();
                crystal.teleport(bossLoc.add(0, 1.5, 0));
                crystal.setBeamTarget(dragon.getLocation().add(0, 5, 0));
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    @EventHandler
    public void onEndermanTarget(EntityTargetEvent e) {
        if (!(e.getEntity() instanceof Enderman)) return;
        if (!(e.getTarget() instanceof Endermite em)) return;
        if (!"§5Endermite".equals(em.getCustomName())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBossAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Endermite em)) return;
        if (!"§5Endermite".equals(em.getCustomName())) return;
        e.setDamage(6);
    }

    @EventHandler
    public void onPlayerHitByBoss(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Endermite em)) return;
        if (!"§5Endermite".equals(em.getCustomName())) return;

        if (e.getEntity() instanceof Player p) {
            Vector dir = p.getLocation().toVector()
                    .subtract(em.getLocation().toVector())
                    .normalize();
            dir.setY(0.5);
            p.setVelocity(dir.multiply(1.2));
        }
    }

    public Location randomLocationAround(Location center, int radius) {
        double x = center.getX() + (random.nextDouble() * 2 - 1) * radius;
        double z = center.getZ() + (random.nextDouble() * 2 - 1) * radius;
        double y = center.getWorld().getHighestBlockYAt((int) x, (int) z) + 1;
        return new Location(center.getWorld(), x, y, z);
    }

    public void spawnTwoRandom(Location center) {
        for (int i = 0; i < 2; i++) {
            long delay = random.nextInt(60) * 20L;
            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnBoss(randomLocationAround(center, 20));
                }
            }.runTaskLater(plugin, delay);
        }
    }
}
