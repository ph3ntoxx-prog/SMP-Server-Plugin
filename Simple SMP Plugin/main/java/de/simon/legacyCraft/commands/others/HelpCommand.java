package de.simon.legacyCraft.commands.others;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        sender.sendMessage(ChatColor.AQUA + "Verfügbare Commands:");
        sender.sendMessage(ChatColor.RESET + "/status <status>  <color>");
        sender.sendMessage(ChatColor.RESET + "/trigger sit");
        sender.sendMessage(ChatColor.RESET + "/status colorhelp");
        sender.sendMessage(ChatColor.RESET + "/status delete");
        sender.sendMessage(ChatColor.RESET + "/imagemap");
        sender.sendMessage(ChatColor.RESET + "/astools");
        sender.sendMessage(ChatColor.RESET + "/tpsbar");

        return true;
    }
}
