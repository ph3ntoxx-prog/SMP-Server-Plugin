package de.simon.legacyCraft.commands.attacks;

import de.simon.legacyCraft.attacks.ShockWave;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpecialAttackCommand implements CommandExecutor {

    private final ShockWave shockWave;

    public SpecialAttackCommand(JavaPlugin plugin, ShockWave shockWave) {
        this.shockWave = shockWave;
        plugin.getCommand("specialattack").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Command kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        shockWave.startSpecialAttack();
        sender.sendMessage("§eSpezialattacke gestartet!");
        return true;
    }
}
