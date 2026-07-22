package de.simon.legacyCraft.status;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) return completions;

        if (args.length == 1) {
            // Vorschläge für das erste Argument
            List<String> options = Arrays.asList("delete", "colorhelp");
            for (String option : options) {
                if (option.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(option);
                }
            }
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("delete") && !args[0].equalsIgnoreCase("colorhelp")) {
            // Vorschläge für Farbe beim zweiten Argument
            List<String> colors = Arrays.asList(
                    "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA",
                    "DARK_RED", "DARK_PURPLE", "GOLD", "GRAY",
                    "DARK_GRAY", "BLUE", "GREEN", "AQUA",
                    "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
            );
            for (String color : colors) {
                if (color.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(color);
                }
            }
        }

        return completions;
    }
}