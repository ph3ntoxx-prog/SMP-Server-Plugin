package de.simon.legacyCraft.listener.nickname;

import de.simon.legacyCraft.nickname.NicknameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NicknameListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        var player = event.getPlayer();

        // 🔴 WICHTIG: Wenn KEIN Nickname gesetzt ist → nichts ändern
        if (!NicknameManager.has(player.getUniqueId())) {
            return;
        }

        String nickname = NicknameManager.get(player.getUniqueId(), player.getName());
        event.setFormat("§7" + nickname + " §8» §f" + event.getMessage());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        var dead = event.getEntity();
        var killer = dead.getKiller();

        if (!NicknameManager.has(killer.getUniqueId())) return;

        String deadName = NicknameManager.get(dead.getUniqueId(), dead.getName());

        if (killer != null) {
            String killerName = NicknameManager.get(killer.getUniqueId(), killer.getName());
            event.setDeathMessage("§c" + deadName + " §7wurde von §c" + killerName + " §7getötet.");
        } else {
            event.setDeathMessage("§c" + deadName + " §7ist gestorben.");
        }
    }
}
