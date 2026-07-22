package de.simon.legacyCraft.commands.nickname;

import de.simon.legacyCraft.nickname.NicknameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NicknameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage("§c/nickname <Name> | /nickname reset");
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            NicknameManager.remove(player.getUniqueId());
            player.sendMessage("§aNickname zurückgesetzt.");
            return true;
        }

        String nickname = String.join(" ", args);
        NicknameManager.set(player.getUniqueId(), nickname);
        player.sendMessage("§aDein Nickname ist jetzt §e" + nickname);
        return true;
    }
}
