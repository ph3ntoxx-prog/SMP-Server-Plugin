package de.deinplugin.leash;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeashListener implements Listener {

    // Map: Zielspieler -> Spieler, der ihn angeleint hat
    private final Map<UUID, UUID> leashed = new HashMap<>();

    // Rechtsklick mit Leine -> anleinen / losleinen
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player holder = event.getPlayer();

        // Leine in Hand?
        if (holder.getInventory().getItemInMainHand().getType() != Material.LEAD) return;

        // Prüfen, ob Spieler bereits angeleint ist
        boolean alreadyLeashed = leashed.containsKey(target.getUniqueId());

        if (alreadyLeashed && leashed.get(target.getUniqueId()).equals(holder.getUniqueId())) {
            // Nur losleinen, wenn es der gleiche Halter ist
            leashed.remove(target.getUniqueId());
            target.sendMessage("§aDu wurdest losgeleint.");
            holder.sendMessage("§aDu hast " + target.getName() + " losgelassen.");
        } else if (!alreadyLeashed) {
            // Anleinen
            leashed.put(target.getUniqueId(), holder.getUniqueId());
            target.sendMessage("§cDu wurdest angeleint!");
            holder.sendMessage("§aDu hast " + target.getName() + " angeleint.");
        } else {
            // Spieler ist bereits von jemand anderem angeleint
            holder.sendMessage("§cDieser Spieler ist bereits angeleint.");
        }

        event.setCancelled(true);
    }

    // Spieler wird zurückgezogen, wenn zu weit weg
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player target = event.getPlayer();

        if (!leashed.containsKey(target.getUniqueId())) return;

        Player holder = Bukkit.getPlayer(leashed.get(target.getUniqueId()));
        if (holder == null) return;

        double distance = target.getLocation().distance(holder.getLocation());

        // Abstand > 5 Blöcke -> Spieler wird sanft herangezogen
        if (distance > 5) {
            Vector pull = holder.getLocation().toVector()
                    .subtract(target.getLocation().toVector())
                    .normalize()
                    .multiply(0.4);

            target.setVelocity(pull);
        }
    }
}
