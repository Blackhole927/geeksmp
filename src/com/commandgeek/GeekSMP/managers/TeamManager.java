package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Setup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Set;

public class TeamManager {

    Team team;

    public TeamManager(String name) {
        Team team = null;
        if (Bukkit.getScoreboardManager() != null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name);
            if (team == null)
                team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(name);
        }
        this.team = team;
    }

    public void color(ChatColor color) {
        team.setColor(color);
    }

    public void prefix(String prefix) {
        team.setPrefix(prefix);
    }

    public void join(Player player) {
        team.addEntry(player.getName());
    }

    public boolean contains(Player player) {
        return team.hasEntry(player.getName());
    }

    public static String endsWith(String name) {
        if (Bukkit.getScoreboardManager() != null) {
            Set<Team> teams = Bukkit.getScoreboardManager().getMainScoreboard().getTeams();
            for (Team team : teams) {
                if (team.getName().replaceAll("^[0-9]+_", "").equalsIgnoreCase(name)) {
                    return team.getName();
                }
            }
        }
        return null;
    }

    public static Team getPlayerTeam(Player player) {
        return Bukkit.getScoreboardManager() != null ? Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()) : null;
    }

    public static boolean isUndead(Player player) {
        Team team = TeamManager.getPlayerTeam(player);
        if (team != null) {
            return team.getName().replaceAll("^[0-9]+_", "").equalsIgnoreCase(getLast());
        }
        return false;
    }

    public static String getLast() {
        String last = null;
        ConfigurationSection section = Main.config.getConfigurationSection("groups");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                last = key;
            }
        }
        return last;
    }

    public static boolean isAlive(Player player) {
        return (Main.alive.getStringList("alive").contains(player.getUniqueId().toString()));
    }

    public static boolean isRevived(Player player) {
        String name = Main.config.getString("groups." + getLast() + ".revive-group");
        Team team = TeamManager.getPlayerTeam(player);
        if (team != null) {
            return team.getName().replaceAll("^[0-9]+_", "").equalsIgnoreCase(name);
        }
        return false;
    }

    public static void revive(Player player) {
        if (!isAlive(player)) {
            List<String> alive = Main.alive.getStringList("alive");
            alive.add(player.getUniqueId().toString());
            Main.alive.set("alive", alive);
            ConfigManager.saveData("alive.yml", Main.alive);
        }
    }

    public static void unrevive(Player player) {
        if (!isAlive(player)) return;
        List<String> alive = Main.alive.getStringList("alive");
        alive.remove(player.getUniqueId().toString());
        Main.alive.set("alive", alive);
        ConfigManager.saveData("alive.yml", Main.alive);
        Setup.updatePlayerRole(player);
    }

    public static boolean isStaff(Player player) {
        Team team = TeamManager.getPlayerTeam(player);
        if (team == null) return false;
        String name = team.getName().replaceAll("^[0-9]+_", "");
        if (Main.config.contains("groups." + name + ".status")) {
            String status = Main.config.getString("groups." + name + ".status");
            return status != null && status.equalsIgnoreCase("staff");
        }
        return false;
    }
}
