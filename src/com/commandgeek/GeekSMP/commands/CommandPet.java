package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandPet implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.pet") && (TeamManager.isUndead(player) || TeamManager.isRevived(player))) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<OfflinePlayer> pets = new ArrayList<>();
                for (String key : Main.pets.getKeys(false)) {
                    List<String> owners = Main.pets.getStringList(key);
                    if (owners.contains(player.getUniqueId().toString())) {
                        pets.add(Bukkit.getOfflinePlayer(UUID.fromString(key)));
                    }
                }
                new MessageManager("pet-list-header").send(player);
                if (pets.size() == 0) {
                    new MessageManager("pet-list-empty").send(player);
                } else {
                    for (OfflinePlayer pet : pets) {
                        new MessageManager("pet-list-item")
                                .replace("%player%", pet.getName())
                                .send(player);
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("clear")) {
                for (String key : Main.pets.getKeys(false)) {
                    List<String> owners = Main.pets.getStringList(key);
                    if (owners.contains(player.getUniqueId().toString())) {
                        Main.pets.set(key, null);
                    }
                }
                ConfigManager.saveData("pets.yml", Main.pets);
                new MessageManager("pet-clear").send(player);
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                OfflinePlayer target = EntityManager.getOfflinePlayer(args[1]);
                if (target == null) {
                    new MessageManager("invalid-player")
                            .replace("%player%", args[1])
                            .send(player);
                    return true;
                }
                MorphManager.pet(target, player);
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                OfflinePlayer target = EntityManager.getOfflinePlayer(args[1]);
                if (target == null) {
                    new MessageManager("invalid-player")
                            .replace("%player%", args[1])
                            .send(player);
                    return true;
                }
                MorphManager.unpet(target, player);
                return true;
            }
        }

        new MessageManager("invalid-arguments").send(player);
        return true;
    }
}
