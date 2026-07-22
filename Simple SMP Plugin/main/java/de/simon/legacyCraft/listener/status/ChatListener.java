package de.simon.legacyCraft.listener.status;

import de.simon.legacyCraft.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Chat-Format muss thread-safe sein → Daten vorher holen
        String[] status = Main.getInstance().getPlayerStatus(player.getName());

        String statusName = "";
        ChatColor color = ChatColor.WHITE;

        if (status != null && status.length >= 2) {
            statusName = status[0] != null ? status[0] : "";

            try {
                color = ChatColor.valueOf(status[1]);
            } catch (Exception ignored) {
                color = ChatColor.WHITE;
            }
        }

        String prefix = "";
        if (!statusName.isEmpty()) {
            prefix = ChatColor.WHITE + "[" + color + statusName.toUpperCase() + ChatColor.WHITE + "] ";
        }

        event.setFormat(prefix + ChatColor.WHITE + "<" + "%1$s" + ChatColor.WHITE + "> " + ChatColor.RESET + "%2$s");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(ChatColor.YELLOW + player.getName() + " joined the game!");

        String[] status = Main.getInstance().getPlayerStatus(player.getName());

        String statusName = "";
        ChatColor color = ChatColor.WHITE;

        if (status != null && status.length >= 2) {
            statusName = status[0] != null ? status[0] : "";

            try {
                color = ChatColor.valueOf(status[1]);
            } catch (Exception ignored) {
                color = ChatColor.WHITE;
            }
        }

        String tabName =
                (!statusName.isEmpty()
                        ? "[" + color + statusName.toUpperCase() + ChatColor.WHITE + "] "
                        : "")
                        + player.getName();

        player.setPlayerListName(tabName);

        if (!statusName.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "Dein Status ist " + color + statusName.toUpperCase() + ChatColor.GRAY + ".");
        } else {
            player.sendMessage(ChatColor.GRAY + "Du hast keinen Status gesetzt.");
        }
    }
}
