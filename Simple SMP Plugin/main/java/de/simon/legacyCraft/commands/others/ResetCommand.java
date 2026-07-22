package de.simon.legacyCraft.commands.others;

import de.simon.legacyCraft.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (sender.isOp()) {

            Main.getInstance().getDragonEgg().resetFirstCollected();
            Main.getInstance().getLogger().info("Resttet");
        }else {

        }

        return true;

    }
}
