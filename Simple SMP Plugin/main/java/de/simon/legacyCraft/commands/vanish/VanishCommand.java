package de.simon.legacyCraft.commands.vanish;

import de.simon.legacyCraft.Main;
import de.simon.legacyCraft.vanish.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Command ausführen!");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage("§cDazu hast du keine Rechte.");
            return true;
        }

        boolean vanish = !VanishManager.isVanished(player);
        VanishManager.setVanished(player, vanish);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (vanish) {
                if (!other.isOp()) {
                    other.hidePlayer(Main.getInstance(), player);
                }
            } else {
                other.showPlayer(Main.getInstance(), player);
            }
        }

        player.setInvisible(vanish);
        player.setAllowFlight(vanish);
        player.setFlying(vanish);

        player.sendMessage(vanish
                ? "§7Du bist jetzt §aunsichtbar§7."
                : "§7Du bist jetzt §csichtbar§7.");

        return true;
    }
}
