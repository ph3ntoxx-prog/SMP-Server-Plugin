package de.simon.legacyCraft.listener.tpsbar;

import de.simon.legacyCraft.tpsbar.TPSBarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final TPSBarManager barManager;


    public PlayerListener(TPSBarManager barManager) {
        this.barManager = barManager;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        barManager.removePlayer(event.getPlayer());
    }
}
