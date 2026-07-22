package de.simon.legacyCraft.egg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class DragonEgg implements Listener {

    private final JavaPlugin plugin;
    private boolean firstCollected = false;
    private final List<String> firstCollectors = Arrays.asList(
            "EinSimonlein", "Nuvioo", "Nyox273", "JoJona1708", "AgentWolf8", "Schwarzer_luan88", "Whuoasi", "LH_FAN44", "Noah_TVn", "Lano091", "Mexxar_0815YT"
    );

    public DragonEgg(JavaPlugin plugin) {
        this.plugin = plugin;
        startGlowTask();
    }

    @EventHandler
    public void onEggPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        if (item.getType() != Material.DRAGON_EGG) return;

        // Prüfen, ob Spieler das Ei aufnehmen darf
        if (!canPickup(player)) {
            event.setCancelled(true);
            return; // NICHT senden
        }

        // Nickname oder echter Name
        String name = de.simon.legacyCraft.nickname.NicknameManager.has(player.getUniqueId())
                ? de.simon.legacyCraft.nickname.NicknameManager.get(player.getUniqueId(), player.getName())
                : player.getName();

        // Koordinaten
        int x = event.getItem().getLocation().getBlockX();
        int y = event.getItem().getLocation().getBlockY();
        int z = event.getItem().getLocation().getBlockZ();

        // Nachricht an alle Spieler
        Bukkit.getOnlinePlayers().forEach(p ->
                p.sendMessage(ChatColor.AQUA + name + " hat das Drachenei bei " +
                        ChatColor.GOLD + x + " " + y + " " + z + ChatColor.AQUA + " aufgesammelt!")
        );

        Bukkit.getLogger().info(name + " hat das Drachenei bei" + x + " " + y + " " + z + " aufgesammelt!");
    }

    // Verhindert, dass das Ei in Enderchest oder Shulker kommt
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && item.getType() == Material.DRAGON_EGG) {
            InventoryType type = event.getInventory().getType();
            if (type == InventoryType.ENDER_CHEST || type == InventoryType.SHULKER_BOX) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(ChatColor.RED + "Das Drachenei kann hier nicht platziert werden!");
            }
        }
    }

    // Legt das Ei beim Ausloggen auf den Block und entfernt es aus dem Inventar
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.DRAGON_EGG) {
                Block block = player.getLocation().getBlock();
                block.setType(Material.DRAGON_EGG);

                player.getInventory().setItem(i, null);

                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();

                // Nachricht an alle Spieler
                Bukkit.getOnlinePlayers().forEach(p ->
                        p.sendMessage(ChatColor.AQUA + "Das Drachenei ist jetzt verfügbar bei: " + ChatColor.GOLD + x + " " + y + " " + z)
                );
                Bukkit.getLogger().info(ChatColor.AQUA + "Das Drachenei ist jetzt verfügbar bei: " + ChatColor.GOLD + x + " " + y + " " + z);

                break;
            }
        }
    }

    // Glow-Effekt regelmäßig prüfen
    private void startGlowTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean hasEgg = Arrays.stream(player.getInventory().getContents())
                        .anyMatch(item -> item != null && item.getType() == Material.DRAGON_EGG);
                if (hasEgg) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false));
                }
            }
        }, 0L, 1L); // jede Tick
    }

    // Methode zum Abgeben des Eis beim Aufnehmen
    public boolean canPickup(Player player) {
        if (!firstCollected) {
            if (firstCollectors.contains(player.getName())) {
                firstCollected = true;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void resetFirstCollected() {
        this.firstCollected=false;
    }

}