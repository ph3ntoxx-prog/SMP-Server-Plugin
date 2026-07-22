package de.simon.legacyCraft.listener.nether;

import de.simon.legacyCraft.nether.NetherManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalListener implements Listener {

    private final NetherManager netherManager;

    public PortalListener(NetherManager netherManager) {
        this.netherManager = netherManager;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerPortalEvent.TeleportCause.NETHER_PORTAL) {
            if (!netherManager.isNetherOpen()) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.sendMessage("§cDer Nether ist momentan geschlossen!");
            }
        }
    }
}