package de.simon.legacyCraft.commands.attacks;

import de.simon.legacyCraft.Main;
import de.simon.legacyCraft.bosses.EndermiteBoss;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnEndermiteBossCommand implements CommandExecutor {

    private final Main plugin;

    public SpawnEndermiteBossCommand(Main plugin) {
        this.plugin = plugin;
        if (plugin.getCommand("spawnendermiteboss") != null) {
            plugin.getCommand("spawnendermiteboss").setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Boss spawnen.");
            return true;
        }

        Location loc = player.getLocation();
        new EndermiteBoss(plugin).spawnBoss(loc);
        player.sendMessage("§cEndermite Boss gespawnt!");
        return true;
    }
}
