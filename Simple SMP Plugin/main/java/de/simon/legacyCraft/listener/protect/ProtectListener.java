package de.simon.legacyCraft.listener.protect;

import de.simon.legacyCraft.protect.LogManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class ProtectListener implements Listener {

    private final LogManager logManager;

    // 🔥 EINMAL erstellen – nicht pro Klick
    private static final Set<Material> INVENTORY_BLOCKS = EnumSet.of(
            // Furnaces
            Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER,
            // Shulker Boxes
            Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            // Shelves / Bookshelves
            Material.OAK_SHELF, Material.SPRUCE_SHELF, Material.BIRCH_SHELF, Material.JUNGLE_SHELF,
            Material.ACACIA_SHELF, Material.DARK_OAK_SHELF, Material.MANGROVE_SHELF, Material.CHERRY_SHELF,
            Material.CHISELED_BOOKSHELF,
            // Container
            Material.CHEST, Material.BARREL, Material.JUKEBOX, Material.LECTERN
    );

    public ProtectListener(LogManager logManager) {
        this.logManager = logManager;
    }

    // ---------------- Block Events ----------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        logManager.addLog(
                event.getBlock(),
                event.getPlayer().getName(),
                "Block gesetzt"
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        logManager.addLog(
                event.getBlock(),
                event.getPlayer().getName(),
                "Block abgebaut"
        );
    }

    // ---------------- Inventory Events ----------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getLocation() == null) return;

        Block block = event.getInventory().getLocation().getBlock();
        if (!INVENTORY_BLOCKS.contains(block.getType())) return;

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        String actionMessage = null;

        if (event.isShiftClick()) {
            if (event.getClickedInventory() == player.getInventory()) {
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    actionMessage = ChatColor.GREEN + ">>" + clickedItem.getType() + " x" + clickedItem.getAmount();
                }
            } else if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                actionMessage = ChatColor.RED + "<< " + clickedItem.getType() + " x" + clickedItem.getAmount();
            }
        } else {
            if (event.getClickedInventory() == event.getInventory()) {
                if (cursor != null && cursor.getType() != Material.AIR) {
                    actionMessage = ChatColor.GREEN + ">>" + cursor.getType() + " x" + cursor.getAmount();
                } else if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    actionMessage = ChatColor.RED + "<< " + clickedItem.getType() + " x" + clickedItem.getAmount();
                }
            }
        }

        if (actionMessage != null) {
            logManager.addLog(block, player.getName(), actionMessage);
        }
    }
}
