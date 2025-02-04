package com.commandgeek.GeekSMP;

import com.commandgeek.GeekSMP.commands.*;
import com.commandgeek.GeekSMP.listeners.*;
import com.commandgeek.GeekSMP.listeners.discord.DiscordMessageCreateListener;
import com.commandgeek.GeekSMP.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;

import java.util.List;

public class Main extends JavaPlugin {

    public static Main instance;
    public static FileConfiguration config;
    public static FileConfiguration messages;
    public static FileConfiguration info;
    public static FileConfiguration stats;
    public static FileConfiguration morphs;
    public static FileConfiguration alive;
    public static FileConfiguration muted;
    public static FileConfiguration banned;
    public static FileConfiguration linked;
    public static FileConfiguration locked;
    public static FileConfiguration trusted;
    public static FileConfiguration pets;

    public static List<String> bannedWords;

    public static DiscordApi discordAPI;
    public static String botPrefix;

    @Override
    public void onEnable() {

        instance = this;

        // Print
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "==================");
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + " Geek SMP Plugin");
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + " by CommandGeek");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "==================");

        // Register Commands
        Setup.registerCommand("revive", new CommandRevive(), new TabPlayer());
        Setup.registerCommand("unrevive", new CommandUnrevive(), new TabPlayer());
        Setup.registerCommand("mute", new CommandMute(), new TabOfflinePlayer());
        Setup.registerCommand("mutelist", new CommandMuteList(), new TabEmpty());
        Setup.registerCommand("unmute", new CommandUnmute(), new TabUnmute());
        Setup.registerCommand("ban", new CommandBan(), new TabOfflinePlayer());
        Setup.registerCommand("banlist", new CommandBanList(), new TabEmpty());
        Setup.registerCommand("unban", new CommandUnban(), new TabUnban());
        Setup.registerCommand("die", new CommandDie(), new TabEmpty());
        Setup.registerCommand("gmc", new CommandGmc(), new TabPlayer());
        Setup.registerCommand("gms", new CommandGms(), new TabPlayer());
        Setup.registerCommand("gma", new CommandGma(), new TabPlayer());
        Setup.registerCommand("gmsp", new CommandGmsp(), new TabPlayer());
        Setup.registerCommand("rules", new CommandRules(), new TabEmpty());
        Setup.registerCommand("info", new CommandInfo(), new TabEmpty());
        Setup.registerCommand("help", new CommandHelp(), new TabEmpty());
        Setup.registerCommand("code", new CommandCode(), new TabEmpty());
        Setup.registerCommand("discord", new CommandDiscord(), new TabEmpty());
        Setup.registerCommand("twitch", new CommandTwitch(), new TabEmpty());
        Setup.registerCommand("map", new CommandMap(), new TabEmpty());
        Setup.registerCommand("reloadsmp", new CommandReloadSmp(), new TabEmpty());
        Setup.registerCommand("changelog", new CommandChangeLog(), new TabChangeLog());
        Setup.registerCommand("unlink", new CommandUnlink(), new TabOfflinePlayer());
        Setup.registerCommand("purgechat", new CommandPurgeChat(), new TabEmpty());
        Setup.registerCommand("purgeteams", new CommandPurgeTeams(), new TabEmpty());
        Setup.registerCommand("feed", new CommandFeed(), new TabPlayer());
        Setup.registerCommand("heal", new CommandHeal(), new TabPlayer());
        Setup.registerCommand("tphere", new CommandTpHere(), new TabPlayer());
        Setup.registerCommand("tp", new CommandTp(), new TabPlayer());
        Setup.registerCommand("msg", new CommandMsg(), new TabPlayer());
        Setup.registerCommand("reply", new CommandReply(), new TabEmpty());
        Setup.registerCommand("msgtoggle", new CommandMsgToggle(), new TabEmpty());
        Setup.registerCommand("spy", new CommandSpy(), new TabEmpty());
        Setup.registerCommand("track", new CommandTrack(), new TabTrack());
        Setup.registerCommand("seeinv", new CommandSeeInv(), new TabPlayer());
        Setup.registerCommand("lookup", new CommandLookup(), new TabOfflinePlayer());
        Setup.registerCommand("playerinfo", new CommandPlayerInfo(), new TabOfflinePlayer());
        Setup.registerCommand("stats", new CommandStats(), new TabEmpty());
        Setup.registerCommand("trust", new CommandTrust(), new TabTrust());
        Setup.registerCommand("trustlist", new CommandTrustList(), new TabEmpty());
        Setup.registerCommand("untrust", new CommandUntrust(), new TabUntrust());
        Setup.registerCommand("pet", new CommandPet(), new TabPet());
        Setup.registerCommand("inspect", new CommandInspect(), new TabEmpty());
        Setup.registerCommand("afk", new CommandAfk(), new TabEmpty());
        Setup.registerCommand("reason", new CommandReason(), new TabOfflinePlayer());

        // Create Files
        ConfigManager.createDefaultConfig("config.yml");
        ConfigManager.createDefaultConfig("messages.yml");
        ConfigManager.createDefaultConfig("info.yml");
        ConfigManager.createData("stats.yml");
        ConfigManager.createData("morphs.yml");
        ConfigManager.createData("alive.yml");
        ConfigManager.createData("muted.yml");
        ConfigManager.createData("banned.yml");
        ConfigManager.createData("linked.yml");
        ConfigManager.createData("locked.yml");
        ConfigManager.createData("trusted.yml");
        ConfigManager.createData("pets.yml");

        Main.config = ConfigManager.loadConfig("config.yml");
        Main.messages = ConfigManager.loadConfig("messages.yml");
        Main.info = ConfigManager.loadConfig("info.yml");
        Main.stats = ConfigManager.loadData("stats.yml");
        Main.morphs = ConfigManager.loadData("morphs.yml");
        Main.alive = ConfigManager.loadData("alive.yml");
        Main.muted = ConfigManager.loadData("muted.yml");
        Main.banned = ConfigManager.loadData("banned.yml");
        Main.linked = ConfigManager.loadData("linked.yml");
        Main.locked = ConfigManager.loadData("locked.yml");
        Main.trusted = ConfigManager.loadData("trusted.yml");
        Main.pets = ConfigManager.loadData("pets.yml");

        // Register Events
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new QuitListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MoveListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), this);

        // Register Recipes
        LockManager.registerRecipe();

        // Discord
        botPrefix = config.getString("discord.prefix");

        // Register Discord Bot
        String token = config.getString("discord.bot-token");
        new DiscordApiBuilder()
                .setToken(token)
                .setAllIntentsExcept(Intent.GUILD_PRESENCES, Intent.GUILD_MESSAGE_TYPING)
                .login()
                .thenAccept(this::onConnectToDiscord)
                .exceptionally(error -> {
                    getLogger().warning("Failed to connect to Discord!");
                    return null;
                });

        // Reload
        new BukkitRunnable() {
            public void run() {
                if (discordAPI != null) {
                    Setup.reload();
                    new MessageManager("smp-chat-start").sendDiscord(DiscordManager.smpChatChannel);
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    private void onConnectToDiscord(DiscordApi api) {
        Main.discordAPI = api;

        getLogger().info("Connected to Discord as " + api.getYourself().getDiscriminatedName());

        api.addListener(new DiscordMessageCreateListener());
        api.updateActivity(ActivityType.WATCHING, "GeekSMP");
    }

    @Override
    public void onDisable() {

        // Unregister Discord Bot
        if (discordAPI != null) {
            discordAPI = null;
        }

        // Delete all Morphs
        for (Player online : Bukkit.getOnlinePlayers()) {
            new MorphManager(online).unmorph();
            online.kickPlayer("Server Restarting");
        }

        new MessageManager("smp-chat-stop").sendDiscord(DiscordManager.smpChatChannel);
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "DO NOT RELOAD THIS THE GEEKSMP PLUGIN. ALWAYS RESTART THE SERVER. SHUTTING DOWN");
        getServer().shutdown();
    }
}
