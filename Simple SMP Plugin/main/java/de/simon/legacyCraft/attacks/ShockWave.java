package de.simon.legacyCraft.attacks;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ShockWave implements Listener {

    private final JavaPlugin plugin;
    private final Map<FallingBlock, Integer> blocks = new HashMap<>();

    public ShockWave(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void startSpecialAttack() {
        World end = Bukkit.getWorld("world_the_end");
        if (end == null) return;

        int y = end.getHighestBlockYAt(0, 0);
        Location center = new Location(end, 0.5, y + 3, 0.5);

        spawnBlinkingFroglight(center);
    }

    public void startRandomSpecialAttacks(EnderDragon dragon) {
        if (dragon == null || dragon.isDead()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (dragon.isDead()) {
                    cancel();
                    return;
                }

                startSpecialAttack();

                int nextDelay = 120 + new Random().nextInt(180);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        startRandomSpecialAttacks(dragon);
                    }
                }.runTaskLater(plugin, nextDelay * 20L);
            }
        }.runTaskLater(plugin, 20L);
    }

    private void spawnBlinkingFroglight(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        FallingBlock block = world.spawnFallingBlock(
                center,
                Material.OCHRE_FROGLIGHT.createBlockData()
        );
        block.setDropItem(false);
        block.setGravity(false);

        blocks.put(block, 100);

        new BukkitRunnable() {
            int ticks = 0;
            boolean toggle = false;

            @Override
            public void run() {
                if (!block.isValid()) {
                    blocks.remove(block);
                    cancel();
                    return;
                }

                ticks++;

                boolean fast = ticks > 60;
                if ((fast && ticks % 4 == 0) || (!fast && ticks % 20 == 0)) {
                    toggle = !toggle;
                    block.setBlockData(
                            (toggle ? Material.PEARLESCENT_FROGLIGHT : Material.OCHRE_FROGLIGHT)
                                    .createBlockData()
                    );

                    for (Player p : world.getPlayers()) {
                        if (p.getLocation().distanceSquared(center) < 400) {
                            p.playSound(
                                    p.getLocation(),
                                    Sound.BLOCK_WOODEN_BUTTON_CLICK_ON,
                                    SoundCategory.BLOCKS,
                                    1f,
                                    1f
                            );
                        }
                    }
                }

                if (ticks == 100) {
                    block.remove();
                    blocks.remove(block);
                    createShockwave(center);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow arrow)) return;
        if (!(e.getHitEntity() instanceof FallingBlock fb)) return;
        if (!blocks.containsKey(fb)) return;

        arrow.remove();

        int hp = blocks.get(fb) - 8;
        blocks.put(fb, hp);

        fb.getWorld().playSound(
                fb.getLocation(),
                Sound.BLOCK_AMETHYST_BLOCK_BREAK,
                2f, 1f
        );

        if (hp <= 0) {
            fb.remove();
            blocks.remove(fb);
        }
    }

    private void createShockwave(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        new BukkitRunnable() {
            double radius = 0;
            final double max = 100;
            final double thickness = 2;

            @Override
            public void run() {
                if (radius > max) {
                    cancel();
                    return;
                }

                for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 48) {
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    Location loc = new Location(world, x, center.getY() - 7, z);

                    world.spawnParticle(Particle.LAVA, loc, 3, 0.2, 0.5, 0.2, 0);
                }

                for (Player p : world.getPlayers()) {
                    double dx = p.getX() - center.getX();
                    double dz = p.getZ() - center.getZ();
                    double distSq = dx * dx + dz * dz;

                    if (distSq >= (radius - thickness) * (radius - thickness)
                            && distSq <= (radius + thickness) * (radius + thickness)
                            && p.getY() <= center.getY() - 3) {

                        Vector dir = p.getLocation().toVector()
                                .subtract(center.toVector())
                                .normalize();

                        p.setVelocity(dir.multiply(0.8).setY(2.0));
                        p.damage(10);

                        world.playSound(
                                p.getLocation(),
                                Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
                                1f, 0.6f
                        );
                    }
                }

                radius += 1.0;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
