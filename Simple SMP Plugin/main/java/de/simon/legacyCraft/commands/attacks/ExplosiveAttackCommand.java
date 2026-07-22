package de.simon.legacyCraft.commands.attacks;

import de.simon.legacyCraft.attacks.ExplosiveAttack;
import de.simon.legacyCraft.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExplosiveAttackCommand implements CommandExecutor {

    private final ExplosiveAttack attack;
    private final Main plugin;

    public ExplosiveAttackCommand(Main plugin) {
        this.plugin = plugin;
        this.attack = new ExplosiveAttack(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        World end = Bukkit.getWorld("world_the_end");
        if (end == null) {
            sender.sendMessage("§cEnd world not found (world_the_end).");
            return true;
        }

        int y = end.getHighestBlockYAt(0, 0);
        Location center = new Location(end, 0.5, y + 3, 0.5);

        // Optional: wenn Sender ein Spieler ist, spawn in dessen Nähe (debug)
        if (args.length > 0 && args[0].equalsIgnoreCase("here") && sender instanceof Player p) {
            attack.spawnExplosiveBlock(p.getLocation().clone().subtract(0, 1, 0));
            sender.sendMessage("§aSpawned explosive block at your location.");
            return true;
        }

        attack.spawnExplosiveBlock(center);
        sender.sendMessage("§aExplosive attack spawned at End center.");
        return true;
    }
}
