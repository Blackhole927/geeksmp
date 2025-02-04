package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGms implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.gms") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (sender instanceof Player player && args.length == 0) {
            player.setGameMode(GameMode.SURVIVAL);
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new MessageManager("invalid-player")
                        .replace("%player%", args[0])
                        .send(sender);
                return true;
            }
            target.setGameMode(GameMode.SURVIVAL);
            return true;
        }

        new MessageManager("invalid-arguments").send(sender);
        return true;
    }
}
