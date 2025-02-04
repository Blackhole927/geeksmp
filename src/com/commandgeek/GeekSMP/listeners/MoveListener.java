package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"unused"})
public class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        AfkManager.moved(player);
        if (AfkManager.check(player)) {
            AfkManager.disable(player);
        }

        if (TeamManager.isUndead(player)) {
            if (!MorphManager.isMorphedPlayer(player)) {
                event.setCancelled(true);
            } else if (!EntityManager.isPlayerNear(player.getLocation(), 100)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 4, true, false, false));
            } else {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }

        Entity entity = MorphManager.getEntity(player);
        if (entity != null) {
            entity.teleport(player.getLocation());
            new PacketManager(player).hideEntity(entity);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!player.isSneaking() && player.getInventory().getItemInMainHand().isSimilar(LockManager.lockTool)) {
            new BukkitRunnable() {
                public void run() {
                    LockManager.showLockedLocations(player);
                    if (!player.isSneaking()) {
                        cancel();
                    }
                }
            }.runTaskTimer(Main.instance, 1, 5);
        }
    }
}
