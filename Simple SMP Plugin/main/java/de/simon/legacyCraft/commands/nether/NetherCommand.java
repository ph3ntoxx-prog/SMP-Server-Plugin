package de.simon.legacyCraft.commands.nether;


import de.simon.legacyCraft.nether.NetherManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NetherCommand implements CommandExecutor {

    private final NetherManager netherManager;

    public NetherCommand(NetherManager netherManager) {
        this.netherManager = netherManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Benutze: /nether <open|close>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "open":
                netherManager.setNetherOpen(true);
                sender.sendMessage("§aDer Nether ist jetzt geöffnet!");
                break;
            case "close":
                netherManager.setNetherOpen(false);
                sender.sendMessage("§cDer Nether ist jetzt geschlossen!");
                break;
            default:
                sender.sendMessage("Benutze: /nether <open|close>");
        }

        return true;
    }
}
