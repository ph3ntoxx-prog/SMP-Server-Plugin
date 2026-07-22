package de.simon.legacyCraft.commands.ban;

import de.simon.legacyCraft.ban.BanManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnbanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cKeine Rechte.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§c/unban <Spieler>");
            return true;
        }

        String targetName = args[0];

        // Spieler offline oder online
        Player target = Bukkit.getPlayer(targetName);
        UUID uuid;

        if (target != null) {
            uuid = target.getUniqueId();
        } else {
            // Offline-Spieler UUID abrufen
            uuid = Bukkit.getOfflinePlayer(targetName).getUniqueId();
        }

        if (!BanManager.isBanned(uuid)) {
            sender.sendMessage("§cDieser Spieler ist nicht gebannt.");
            return true;
        }

        BanManager.unban(uuid);
        sender.sendMessage("§aSpieler §e" + targetName + " §awurde entbannt.");
        return true;
    }
}
