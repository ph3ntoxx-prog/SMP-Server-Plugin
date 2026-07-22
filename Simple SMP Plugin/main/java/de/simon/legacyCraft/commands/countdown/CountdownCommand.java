package de.simon.legacyCraft.commands.countdown;

import de.simon.legacyCraft.countdown.CountdownManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CountdownCommand implements CommandExecutor {

    private final CountdownManager countdownManager;

    public CountdownCommand(CountdownManager manager) {
        this.countdownManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("countdown.use")) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung diesen Command auszuführen!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Benutzung: /countdown <Titel> <d> <h> <m> <s> oder /countdown <Titel> <TT.MM.JJJJ> <HH:MM>");
            return true;
        }

        String title = args[0];

        // Datum/Uhrzeit Variante
        if (args[1].contains(".")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Benutzung: /countdown <Titel> <TT.MM.JJJJ> <HH:MM>");
                return true;
            }

            String dateStr = args[1];
            String timeStr = args[2];
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                LocalDateTime target = LocalDateTime.parse(dateStr + " " + timeStr, formatter).minusHours(1);
                LocalDateTime now = LocalDateTime.now();
                long totalSeconds = ChronoUnit.SECONDS.between(now, target);

                if (totalSeconds <= 0) {
                    sender.sendMessage(ChatColor.RED + "Zeit muss größer als 0 sein!");
                    return true;
                }

                // ✅ nur Countdown starten, Meldung bleibt wie bisher
                countdownManager.startCountdown(title, totalSeconds);
                sender.sendMessage(ChatColor.GREEN + "Countdown gestartet!");
                return true;

            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Ungültiges Datum oder Uhrzeit! Format: TT.MM.JJJJ HH:MM");
                return true;
            }
        }

        // Zahlen Variante
        int d = 0, h = 0, m = 0, s = 0;

        try {
            if (args.length > 1) d = Integer.parseInt(args[1]);
            if (args.length > 2) h = Integer.parseInt(args[2]);
            if (args.length > 3) m = Integer.parseInt(args[3]);
            if (args.length > 4) s = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Bitte gültige Zahlen eingeben!");
            return true;
        }

        long totalSeconds =
                (long) d * 86400
                        + (long) h * 3600
                        + (long) m * 60
                        + s;

        if (totalSeconds <= 0) {
            sender.sendMessage(ChatColor.RED + "Zeit muss größer als 0 sein!");
            return true;
        }

        countdownManager.startCountdown(title, totalSeconds);
        sender.sendMessage(ChatColor.GREEN + "Countdown gestartet!");

        return true;
    }
}

