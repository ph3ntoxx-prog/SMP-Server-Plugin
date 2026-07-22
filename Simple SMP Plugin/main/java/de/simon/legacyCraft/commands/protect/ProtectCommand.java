package de.simon.legacyCraft.commands.protect;

import de.simon.legacyCraft.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtectCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.isOp() && !player.hasPermission("protect.spy") ) {
            player.sendMessage("Du hast keine Berechtigung diesen Command auszuführen!");
        }else {
            return Main.getInstance().getLogManager().showBlockLogs(player);
        }

        return true;

    }
}
