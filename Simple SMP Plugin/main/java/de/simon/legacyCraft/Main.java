package de.simon.legacyCraft;

import de.deinplugin.leash.LeashListener;
import de.simon.legacyCraft.ban.BanManager;
import de.simon.legacyCraft.commands.ban.UnbanCommand;
import de.simon.legacyCraft.listener.ban.LoginListener;
import de.simon.legacyCraft.commands.ban.BanCommand;
import de.simon.legacyCraft.commands.nether.NetherCommand;
import de.simon.legacyCraft.commands.protect.ProtectCommand;
import de.simon.legacyCraft.commands.vanish.VanishCommand;
import de.simon.legacyCraft.countdown.CountdownManager;
import de.simon.legacyCraft.see.EnderSeeCommand;
import de.simon.legacyCraft.see.InvSeeCommand;
import de.simon.legacyCraft.status.StatusManager;
import de.simon.legacyCraft.status.StatusTabCompleter;
import de.simon.legacyCraft.attacks.ExplosiveAttack;
import de.simon.legacyCraft.attacks.ShockWave;
import de.simon.legacyCraft.commands.tpsbar.TPSBarCommand;
import de.simon.legacyCraft.commands.attacks.ExplosiveAttackCommand;
import de.simon.legacyCraft.commands.attacks.SpawnEndermiteBossCommand;
import de.simon.legacyCraft.commands.attacks.SpecialAttackCommand;
import de.simon.legacyCraft.commands.countdown.CountdownCommand;
import de.simon.legacyCraft.commands.others.HelpCommand;
import de.simon.legacyCraft.commands.others.ResetCommand;
import de.simon.legacyCraft.commands.status.StatusCommand;
import de.simon.legacyCraft.dragon.DragonListener;
import de.simon.legacyCraft.egg.DragonEgg;
import de.simon.legacyCraft.listener.nether.PortalListener;
import de.simon.legacyCraft.listener.spawnelytra.SpawnElytraListener;
import de.simon.legacyCraft.listener.status.ChatListener;
import de.simon.legacyCraft.listener.tpsbar.PlayerListener;
import de.simon.legacyCraft.listener.protect.ProtectListener;
import de.simon.legacyCraft.nether.NetherManager;
import de.simon.legacyCraft.protect.LogManager;
import de.simon.legacyCraft.tpsbar.TPSBarManager;
import de.simon.legacyCraft.waves.WaveManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import de.simon.legacyCraft.commands.nickname.NicknameCommand;
import de.simon.legacyCraft.listener.nickname.NicknameListener;

public final class Main extends JavaPlugin {

    private static Main instance;
    private WaveManager waveManager;
    private DragonListener dragonListener;
    private ShockWave shockWave;
    private ExplosiveAttack explosiveAttack;
    private CountdownManager countdownManager;
    private DragonEgg dragonEgg;
    private StatusManager statusManager;
    private TPSBarManager barManager;
    private LogManager logManager;
    private Location spawnLocation;
    private double spawnRadius;
    private double boostMultiplier;
    private NetherManager netherManager;


    @Override
    public void onEnable() {
        instance = this;

        BanManager.init(this);

        // Config laden/erstellen
        saveDefaultConfig();

        logManager = new LogManager(this);
        Bukkit.getPluginManager().registerEvents(new ProtectListener( logManager), this);

        statusManager = new StatusManager();

        this.barManager = new TPSBarManager(this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(barManager), this);
        getCommand("tpsbar").setExecutor(new TPSBarCommand(barManager));
        barManager.startUpdating();

        // Status aus Config laden
        if (getConfig().contains("statuses")) {
            for (String playerName : getConfig().getConfigurationSection("statuses").getKeys(false)) {
                String statusName = getConfig().getString("statuses." + playerName + ".name", "");
                String colorName = getConfig().getString("statuses." + playerName + ".color", "WHITE");
                statusManager.setPlayerStatus(playerName, statusName, colorName);
            }
        }

        // Listener & Command registrieren
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        getCommand("status").setExecutor(new StatusCommand());
        getCommand("status").setTabCompleter(new StatusTabCompleter());
        // Tablist für alle Spieler beim Start aktualisieren
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTablist(player);
        }

        dragonListener = new DragonListener();
        Bukkit.getPluginManager().registerEvents(dragonListener, this);

        waveManager = new WaveManager();
        waveManager.start(); // automatisch auf vorhandenen Drachen anwenden

        shockWave = new ShockWave(this);
        new SpecialAttackCommand(this, shockWave);
        getCommand("spawnboss").setExecutor(new SpawnEndermiteBossCommand(this));

        if (getCommand("explodeattack") != null) {
            getCommand("explodeattack").setExecutor(new ExplosiveAttackCommand(this));
        }

        explosiveAttack = new ExplosiveAttack(this);

        getLogger().info("LegacyCraft Serverplugin gestartet!");

        dragonEgg = new DragonEgg(this);
        getServer().getPluginManager().registerEvents(dragonEgg, this);

        countdownManager = new CountdownManager(this);

        getCommand("countdown").setExecutor(new CountdownCommand(countdownManager));

        dragonEgg = new DragonEgg(this);
        getServer().getPluginManager().registerEvents(dragonEgg, this);

        getCommand("commands").setExecutor(new HelpCommand());

        getCommand("regg").setExecutor(new ResetCommand());

        getCommand("spy").setExecutor(new ProtectCommand());

        this.netherManager = new NetherManager();

        this.getCommand("nether").setExecutor(new NetherCommand(netherManager));

        getServer().getPluginManager().registerEvents(new PortalListener(netherManager), this);

        Bukkit.getPluginManager().registerEvents(new SpawnElytraListener(this), this);

        getCommand("invsee").setExecutor(new InvSeeCommand());

        getCommand("endersee").setExecutor(new EnderSeeCommand());

        getCommand("nickname").setExecutor(new NicknameCommand());

        getServer().getPluginManager().registerEvents(new NicknameListener(), this);

        getCommand("vanish").setExecutor(new VanishCommand());

        getCommand("ban").setExecutor(new BanCommand());

        Bukkit.getPluginManager().registerEvents(new LoginListener(), this);

        Bukkit.getPluginManager().registerEvents(new LeashListener(), this);

        getCommand("unban").setExecutor(new UnbanCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("LegacyCraft Serverplugin gestoppt!");
        if (barManager != null) {
            barManager.stopUpdating();
            barManager.removeAllPlayers();
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public DragonListener getDragonListener() {
        return dragonListener;
    }

    public ShockWave getAttackManager() {
        return shockWave;
    }

    public ExplosiveAttack getExplosiveAttack() {
        return explosiveAttack;
    }

    public CountdownManager getCountdownManager() {
        return countdownManager;
    }

    public DragonEgg getDragonEgg() {
        return dragonEgg;
    }

    public String[] getPlayerStatus(String playerName) {
        return statusManager.getPlayerStatus(playerName);
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public NetherManager getNetherManager() {
        return netherManager;
    }

    public void setPlayerStatus(String playerName, String statusName, String colorName) {
        statusManager.setPlayerStatus(playerName, statusName, colorName);

        // Status in Config speichern
        getConfig().set("statuses." + playerName + ".name", statusName);
        getConfig().set("statuses." + playerName + ".color", colorName);
        saveConfig();

        // Tablist direkt aktualisieren
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) updateTablist(player);
    }

    public void removePlayerStatus(String playerName) {
        statusManager.removePlayerStatus(playerName);

        // Status aus Config entfernen
        getConfig().set("statuses." + playerName, null);
        saveConfig();

        // Tablist aktualisieren
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) updateTablist(player);
    }

    public void updateTablist(Player player) {
        String[] status = statusManager.getPlayerStatus(player.getName());
        String statusName = status[0];
        String colorName = status[1];

        ChatColor color;
        try {
            color = ChatColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            color = ChatColor.WHITE;
        }

        String tabName = (!statusName.isEmpty() ? "[" + color + statusName + ChatColor.WHITE + "] " : "") + player.getName();
        player.setPlayerListName(tabName);
    }

}
