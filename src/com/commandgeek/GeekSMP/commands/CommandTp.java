package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTp implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.tp") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new MessageManager("invalid-player")
                        .replace("%player%", args[0])
                        .send(player);
                return true;
            }
            player.teleport(target);
            return true;
        }
        if (args.length == 3) {
            double x = 0;
            double y = 0;
            double z = 0;
            if (args[0].startsWith("~")) x = player.getLocation().getX();
            if (args[1].startsWith("~")) y = player.getLocation().getY();
            if (args[2].startsWith("~")) z = player.getLocation().getZ();
            try {
                if (!args[0].equals("~"))
                    x = x + Double.parseDouble(args[0].replaceAll("^~", ""));
                if (!args[1].equals("~"))
                    y = y + Double.parseDouble(args[1].replaceAll("^~", ""));
                if (!args[2].equals("~"))
                    z = z + Double.parseDouble(args[2].replaceAll("^~", ""));
                player.teleport(new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch()));
                return true;
            } catch (NumberFormatException ignored) {
                new MessageManager("invalid-arguments").send(player);
                return true;
            }
        }

        new MessageManager("invalid-arguments").send(player);
        return true;
    }
}
