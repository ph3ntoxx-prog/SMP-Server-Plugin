package de.simon.legacyCraft.commands.status;

import de.simon.legacyCraft.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StatusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können diesen Command nutzen!");
            return true;
        }

        // /status delete
        if (args.length == 1 && args[0].equalsIgnoreCase("delete")) {
            Main.getInstance().removePlayerStatus(player.getName());        // Status entfernen
            player.sendMessage(ChatColor.GRAY + "Dein Status wurde gelöscht.");  // Feedback an Spieler

            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("colorhelp")) {
            sendColorHelp(player);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Benutzung: /status <status> <color>");
            return true;
        }

        // Mehrwort-Status zusammenfügen
        String statusName = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1));
        String colorName = args[args.length - 1].toUpperCase();

        // Status auf gebannte Wörter prüfen
        List<String> bannedWords = Main.getInstance().getConfig().getStringList("banned-words");
        for (String banned : bannedWords) {
            if (statusName.toLowerCase().contains(banned.toLowerCase())) {
                player.sendMessage(ChatColor.RED + "Ungültige Wörter!");
                return true; // Befehl abbrechen
            }
        }

        // Farbe prüfen
        ChatColor color;
        try {
            color = ChatColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Ungültige Farbe!");
            return true;
        }

        Main.getInstance().setPlayerStatus(player.getName(), statusName.toUpperCase(), colorName);                     // Status speichern
        player.sendMessage(ChatColor.GRAY + "Dein Status ist jetzt " + color + statusName.toUpperCase() + ChatColor.RESET); // Chat-Feedback

        // Tablist direkt aktualisieren
        String tabName = (!statusName.isEmpty() ? "[" + color + statusName.toUpperCase() + ChatColor.WHITE + "] " : "") + player.getName();
        player.setPlayerListName(tabName);

        return true;



    }

    // Farbtabelle für Spieler
    private void sendColorHelp(Player player) {
        player.sendMessage(ChatColor.BLACK + "BLACK " + ChatColor.DARK_BLUE + "DARK_BLUE " + ChatColor.DARK_GREEN + "DARK_GREEN " + ChatColor.DARK_AQUA + "DARK_AQUA");
        player.sendMessage(ChatColor.DARK_RED + "DARK_RED " + ChatColor.DARK_PURPLE + "DARK_PURPLE " + ChatColor.GOLD + "GOLD " + ChatColor.GRAY + "GRAY");
        player.sendMessage(ChatColor.DARK_GRAY + "DARK_GRAY " + ChatColor.BLUE + "BLUE " + ChatColor.GREEN + "GREEN " + ChatColor.AQUA + "AQUA");
        player.sendMessage(ChatColor.RED + "RED " + ChatColor.LIGHT_PURPLE + "LIGHT_PURPLE " + ChatColor.YELLOW + "YELLOW " + ChatColor.WHITE + "WHITE");
    }
}
