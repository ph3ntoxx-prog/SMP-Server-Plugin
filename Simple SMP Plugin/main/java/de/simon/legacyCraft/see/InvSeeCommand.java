package de.simon.legacyCraft.see;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvSeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Command nutzen.");
            return true;
        }

        if (!player.hasPermission("invsee.use")) {
            player.sendMessage("§cDu hast keine Berechtigung dafür.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cBenutzung: /invsee <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage("§cSpieler nicht gefunden oder offline.");
            return true;
        }

        player.openInventory(target.getInventory());
        player.sendMessage("§aDu siehst jetzt das Inventar von §e" + target.getName());

        return true;
    }
}
