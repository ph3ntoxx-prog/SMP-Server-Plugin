package de.simon.legacyCraft.listener.ban;

import de.simon.legacyCraft.ban.BanManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (BanManager.isBanned(uuid)) {
            event.disallow(
                    PlayerLoginEvent.Result.KICK_BANNED,
                    "§cDu bist gebannt!\n\n" +
                            "§7Verbleibend: §c" + BanManager.getRemainingTime(uuid) + "\n" +
                            "§7Bis: §c" +
                            new SimpleDateFormat("dd.MM.yyyy HH:mm")
                                    .format(new Date(BanManager.getEnd(uuid) + 60L * 60L * 1000L)) +
                            "\n§7Grund: §c" + BanManager.getReason(uuid)
            );
        }
    }
}
