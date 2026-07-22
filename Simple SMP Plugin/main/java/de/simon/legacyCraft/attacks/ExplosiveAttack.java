package de.simon.legacyCraft.attacks;

import de.simon.legacyCraft.Main;
import org.bukkit.*;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ExplosiveAttack implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    // 🔧 aktiver Explosivblock → HP
    private final Map<FallingBlock, Integer> explosiveBlocks = new HashMap<>();

    public ExplosiveAttack(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void startRandomExplosions() {
        spawnNext();
    }

    private void spawnNext() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = plugin.getServer().getWorld("world_the_end");
                if (world != null) {
                    int count = 1 + random.nextInt(1);
                    for (int i = 0; i < count; i++) {
                        spawnExplosiveBlock(randomLocation(world));
                    }
                }

                int nextDelay = 60 + random.nextInt(180);
                spawnNextDelayed(nextDelay * 20L);
            }
        }.runTaskLater(plugin, 0L);
    }

    private void spawnNextDelayed(long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnNext();
            }
        }.runTaskLater(plugin, delay);
    }

    private Location randomLocation(World world) {
        int x = random.nextInt(21) - 10;
        int z = random.nextInt(21) - 10;
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y + 10, z);
    }

    public void spawnExplosiveBlock(Location loc) {
        if (loc == null || loc.getWorld() == null) return;

        World world = loc.getWorld();
        Location spawnLoc = loc.clone().add(0.5, 0, 0.5);

        FallingBlock fb = world.spawnFallingBlock(
                spawnLoc,
                Material.CHORUS_FLOWER.createBlockData()
        );
        fb.setDropItem(false);
        fb.setGravity(false);
        fb.setGlowing(true);

        explosiveBlocks.put(fb, 30);

        startBlinkTask(fb, spawnLoc);
    }

    // 🔧 EIN Listener für alle Blöcke
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.Arrow arrow)) return;
        if (!(e.getHitEntity() instanceof FallingBlock fb)) return;
        if (!explosiveBlocks.containsKey(fb)) return;

        e.setCancelled(true);
        arrow.remove();

        int hp = explosiveBlocks.get(fb) - 10;
        explosiveBlocks.put(fb, hp);

        fb.getWorld().playSound(
                fb.getLocation(),
                Sound.BLOCK_AMETHYST_BLOCK_BREAK,
                2f, 1f
        );

        if (hp <= 0) {
            explodeBlock(fb);
        }
    }

    private void startBlinkTask(FallingBlock fb, Location spawnLoc) {
        World world = spawnLoc.getWorld();

        new BukkitRunnable() {
            int ticks = 0;
            boolean toggle = false;

            @Override
            public void run() {
                if (!fb.isValid()) {
                    explosiveBlocks.remove(fb);
                    cancel();
                    return;
                }

                ticks++;

                if (ticks <= 120) {
                    if (ticks % 20 == 0) toggle();
                } else {
                    if (ticks % 6 == 0) toggle();
                }

                if (ticks >= 160) {
                    explodeBlock(fb);
                    cancel();
                }
            }

            private void toggle() {
                toggle = !toggle;
                try {
                    Ageable age = (Ageable) Material.CHORUS_FLOWER.createBlockData();
                    age.setAge(toggle ? 0 : 5);
                    fb.setBlockData(age);
                } catch (Throwable ignored) {}

                for (Player p : world.getPlayers()) {
                    if (p.getLocation().distanceSquared(spawnLoc) < 400) {
                        p.playSound(
                                p.getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_HAT,
                                SoundCategory.PLAYERS,
                                1f,
                                toggle ? 1.5f : 0.5f
                        );
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void explodeBlock(FallingBlock fb) {
        World world = fb.getWorld();
        Location loc = fb.getLocation();

        explosiveBlocks.remove(fb);
        fb.remove();

        world.spawnParticle(Particle.EXPLOSION, loc, 1);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1f);

        spawnFlyingCrystals(loc);
    }

    private void spawnFlyingCrystals(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        int count = 40;
        double maxRadius = 7.0;

        for (int i = 0; i < count; i++) {
            ArmorStand as = world.spawn(center.clone().add(0, 0.5, 0), ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setSmall(true);
                stand.setGravity(true);
                stand.setInvulnerable(false);
                stand.getEquipment().setHelmet(new ItemStack(Material.END_CRYSTAL));
            });

            Vector velocity = new Vector(
                    (random.nextDouble() - 0.5) * 3.0,
                    0.8 + random.nextDouble() * 1.2,
                    (random.nextDouble() - 0.5) * 3.0
            );
            as.setVelocity(velocity);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!as.isValid()) {
                        cancel();
                        return;
                    }

                    if (as.isOnGround() || as.getLocation().getBlock().getType().isSolid()) {
                        Location impact = as.getLocation();

                        world.spawnParticle(Particle.EXPLOSION, impact, 12, 1, 1, 1, 0);
                        world.playSound(impact, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1f);

                        for (Player p : world.getPlayers()) {
                            if (p.getLocation().distance(impact) <= maxRadius) {
                                Vector dir = p.getLocation().toVector()
                                        .subtract(center.toVector())
                                        .normalize();
                                p.setVelocity(dir.multiply(1.2).setY(1.5));
                                p.damage(8.0);
                            }
                        }

                        as.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 3L);
        }
    }
}
