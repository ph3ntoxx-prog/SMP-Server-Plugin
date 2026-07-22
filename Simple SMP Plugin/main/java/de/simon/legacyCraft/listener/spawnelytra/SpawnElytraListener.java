package de.simon.legacyCraft.listener.spawnelytra;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.KeybindComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpawnElytraListener extends BukkitRunnable implements Listener {

    private final Plugin plugin;
    private final int multiplyValue;
    private final double spawnRadiusSquared;
    private final boolean boostEnabled;
    private final World world;
    private final Location spawn;

    private final Set<UUID> flying = new HashSet<>();
    private final Set<UUID> boosted = new HashSet<>();

    public SpawnElytraListener(Plugin plugin) {
        this.plugin = plugin;
        this.multiplyValue = plugin.getConfig().getInt("multiplyValue");
        int spawnRadius = plugin.getConfig().getInt("spawnRadius");
        this.spawnRadiusSquared = spawnRadius * spawnRadius;
        this.boostEnabled = plugin.getConfig().getBoolean("boostEnabled");
        this.world = Bukkit.getWorld(plugin.getConfig().getString("world"));
        this.spawn = world != null ? world.getSpawnLocation() : null;

        this.runTaskTimer(this.plugin, 0L, 10L); // ❗ statt 3 Ticks → 10 reicht völlig
    }

    @Override
    public void run() {
        if (world == null || spawn == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.SURVIVAL) continue;
            if (!player.getWorld().equals(world)) continue;

            boolean inSpawn = isInSpawnRadius(player);

            if (inSpawn && !player.getAllowFlight()) {
                player.setAllowFlight(true);
            } else if (!inSpawn && player.getAllowFlight()) {
                player.setAllowFlight(false);
            }

            UUID id = player.getUniqueId();

            if (flying.contains(id)
                    && !player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isAir()) {

                player.setGliding(false);
                boosted.remove(id);

                Bukkit.getScheduler().runTaskLater(plugin, () -> flying.remove(id), 5L);
            }
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) return;
        if (!isInSpawnRadius(player)) return;

        event.setCancelled(true);
        player.setGliding(true);

        if (boostEnabled) {
            player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    new ComponentBuilder("Drücke ")
                            .append(new KeybindComponent("key.swapOffhand"))
                            .append(" um dich zu boosten")
                            .create()
            );
            flying.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;

        Player player = (Player) event.getEntity();
        if (!flying.contains(player.getUniqueId())) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapItem(PlayerSwapHandItemsEvent event) {
        UUID id = event.getPlayer().getUniqueId();

        if (!boostEnabled) return;
        if (!flying.contains(id)) return;
        if (boosted.contains(id)) return;

        event.setCancelled(true);
        boosted.add(id);
        event.getPlayer().setVelocity(
                event.getPlayer().getLocation().getDirection().multiply(multiplyValue)
        );
    }

    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;

        if (flying.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        flying.remove(id);
        boosted.remove(id);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!player.getWorld().equals(world)) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setGliding(false);
            flying.remove(id);
            boosted.remove(id);
        }
    }

    private boolean isInSpawnRadius(Player player) {
        return player.getLocation().distanceSquared(spawn) <= spawnRadiusSquared;
    }
}
