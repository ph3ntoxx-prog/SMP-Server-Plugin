package de.simon.legacyCraft.commands.tpsbar;

import de.simon.legacyCraft.tpsbar.TPSBarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPSBarCommand implements CommandExecutor {

    private final TPSBarManager barManager;

    public TPSBarCommand(TPSBarManager barManager) {
        this.barManager = barManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen");
            return true;
        }

        if (barManager.isVisible(player)) {
            barManager.removePlayer(player);
            player.sendMessage("§cTPSBar deaktiviert");
        } else {
            barManager.addPlayer(player);
            player.sendMessage("§aTPSBar aktiviert");
        }

        return true;
    }
}
