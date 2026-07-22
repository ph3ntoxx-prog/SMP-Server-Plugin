package de.simon.legacyCraft.commands.ban;

import de.simon.legacyCraft.ban.BanManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class BanCommand implements CommandExecutor {

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p) || !p.isOp()) {
            sender.sendMessage("§cKeine Rechte.");
            return true;
        }

        if (args.length < 3) {
            p.sendMessage("§c/ban <Name> <now|dd.MM.yyyy> <24h|7d|dd.MM.yyyy> [Grund]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage("§cSpieler nicht online.");
            return true;
        }

        long start = parseStart(args[1]);
        long end = parseEnd(args[2], start);

        if (start == -1 || end == -1 || end <= start) {
            p.sendMessage("§cUngültiges Datum oder Dauer.");
            return true;
        }

        String reason = args.length > 3
                ? String.join(" ", Arrays.copyOfRange(args, 3, args.length))
                : "Kein Grund";

        BanManager.ban(target, start, end, reason);

        Bukkit.broadcastMessage(
                "§c" + target.getName() + " §7wurde gebannt bis §c" +
                        FORMAT.format(new Date(end))
        );

        return true;
    }

    private long parseStart(String s) {
        if (s.equalsIgnoreCase("now")) {
            return System.currentTimeMillis();
        }
        try {
            return FORMAT.parse(s).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    private long parseEnd(String s, long start) {
        if (s.matches("\\d+[hd]")) {
            int value = Integer.parseInt(s.replaceAll("\\D", ""));
            return start + (s.endsWith("h")
                    ? value * 60L * 60L * 1000L
                    : value * 24L * 60L * 60L * 1000L);
        }

        try {
            return FORMAT.parse(s).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }
}
