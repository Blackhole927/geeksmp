package com.commandgeek.GeekSMP.menus;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Morph;
import com.commandgeek.GeekSMP.managers.InventoryManager;
import com.commandgeek.GeekSMP.managers.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinMenu {

    public static void open(Player player) {

        Inventory inventory = new InventoryManager(9, "Select Identity")
                .set(2, getItem(1))
                .set(4, getItem(2))
                .set(6, getItem(3))
                .get();

        // Open
        new BukkitRunnable() {
            public void run() {
                player.openInventory(inventory);
            }
        }.runTaskLater(Main.instance, 0);
    }

    private static ItemStack getItem(int index) {
        String material = Main.config.getString("morph-selection.item" + index + ".material");
        if (material != null) {
            ItemManager itemManager = new ItemManager(Material.matchMaterial(material));
            if (Main.config.contains("morph-selection.item" + index + ".name"))
                itemManager.name(Main.config.getString("morph-selection.item" + index + ".name"));
            if (Main.config.contains("morph-selection.item" + index + ".lore"))
                itemManager.paragraph(30, Main.config.getString("morph-selection.item" + index + ".lore"));
            if (Main.config.contains("morph-selection.item" + index + ".skull"))
                itemManager.head(Main.config.getString("morph-selection.item" + index + ".skull"));
            return itemManager.get();
        }
        return null;
    }

    public static void select(Player player, int slot) {
        if (slot == 2) {
            Morph.zombie(player);
            player.closeInventory();
        }

        if (slot == 6) {
            Morph.skeleton(player);
            player.closeInventory();
        }
    }
}
