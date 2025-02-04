package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.Setup;
import com.commandgeek.GeekSMP.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReloadSmp implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("geeksmp.command.reloadsmp")) {
            new MessageManager("no-permission").send(sender);
            return true;
        }

        Setup.reload();
        new MessageManager("reload").send(sender);
        return false;
    }
}
