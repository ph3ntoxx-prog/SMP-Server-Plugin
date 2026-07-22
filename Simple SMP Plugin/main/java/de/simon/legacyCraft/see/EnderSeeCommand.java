package de.simon.legacyCraft.see;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderSeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können das.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage("§cNur OPs dürfen das.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cBenutzung: /endersee <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage("§cSpieler offline oder nicht gefunden.");
            return true;
        }

        player.openInventory(target.getEnderChest());
        player.sendMessage("§aEnderchest von §e" + target.getName() + " §ageöffnet.");
        return true;
    }
}
