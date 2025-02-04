package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.MessageManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandHelp implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.help")) {
            new MessageManager("no-permission").send(sender);
            return true;
        }

        ConfigurationSection section = Main.info.getConfigurationSection("help");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (Main.info.isList("help." + key)) {
                    String header = WordUtils.capitalizeFully(key.replace("-", " "));
                    new MessageManager("info-header").replace("%header%", header).send(sender);
                    for (String item : Main.info.getStringList("help." + key)) {
                        item = item.replaceAll("\\*\\*(\\w+)\\*\\*", "$1");
                        new MessageManager("info-item").replace("%item%", item).send(sender);  
                    }
                }
            }
        }
        return true;
    }
}
