package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.*;

public class LockManager {

    public static ItemStack lockTool = getLockTool();

    public static boolean isLocked(Block block) {
        return Main.locked.contains(getId(block) + ".locked");
    }

    public static boolean isLockedForPlayer(Block block, Player player) {
        if (isLocked(block)) {
            String owner = getLocker(block);
            if (owner != null) {
                return !owner.equalsIgnoreCase(player.getUniqueId().toString());
            }
        }
        return false;
    }

    public static String getLocker(Block block) {
        if (isLocked(block)) {
            return Main.locked.getString(getId(block) + ".locked");
        }
        return null;
    }

    public static boolean isLockable(Block block) {
        Material[] allowed = {Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL, Material.ENDER_CHEST, Material.STONE_BUTTON, Material.OAK_BUTTON, Material.BIRCH_BUTTON, Material.SPRUCE_BUTTON, Material.JUNGLE_BUTTON, Material.DARK_OAK_BUTTON, Material.ACACIA_BUTTON, Material.WARPED_BUTTON, Material.CRIMSON_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON, Material.LEVER, Material.DROPPER, Material.DISPENSER, Material.HOPPER};
        for (Material material : allowed) {
            if (block.getType() == material) {
                return true;
            }
        }
        return false;
    }

    public static void showLockedLocations(Player player) {
        List<List<Location>> locations = getLockedLocationsAroundPlayer(player, 15);
        particleCube(player, locations.get(0), Color.LIME, 0.25);
        particleCube(player, locations.get(1), Color.YELLOW, 0.25);
        particleCube(player, locations.get(2), Color.RED, 0.25);
    }

    public static List<List<Location>> getLockedLocationsAroundPlayer(Player player, double radius) {
        List<Location> owned = new ArrayList<>();
        List<Location> trusted = new ArrayList<>();
        List<Location> locked = new ArrayList<>();

        for (String key : Main.locked.getKeys(false)) {
            String[] c = key.split("=");
            Location loc = new Location(Bukkit.getWorld(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2]), Double.parseDouble(c[3]));
            if (loc.getWorld() == player.getLocation().getWorld() && loc.distance(player.getLocation()) <= radius) {
                Block block = loc.getBlock();
                String owner = getLocker(block);

                if (owner != null) {
                    if (owner.equals(player.getUniqueId().toString())) {
                        owned.add(loc);
                    } else if (isTrustedBy(player, Bukkit.getOfflinePlayer(UUID.fromString(owner)))) {
                        trusted.add(loc);
                    } else {
                        locked.add(loc);
                    }
                }
            }
        }

        return Arrays.asList(owned, trusted, locked);
    }

    public static void particleCube(Player player, List<Location> locations, Color color, double distance) {
        List<Location> result = new ArrayList<>();
        for (Location loc : locations) {
            Location corner1 = loc.clone();
            Location corner2 = loc.clone().add(1, 1, 1);

            double minX = Math.min(corner1.getX(), corner2.getX());
            double minY = Math.min(corner1.getY(), corner2.getY());
            double minZ = Math.min(corner1.getZ(), corner2.getZ());
            double maxX = Math.max(corner1.getX(), corner2.getX());
            double maxY = Math.max(corner1.getY(), corner2.getY());
            double maxZ = Math.max(corner1.getZ(), corner2.getZ());

            for (double x = minX; x <= maxX; x += distance) {
                for (double y = minY; y <= maxY; y += distance) {
                    for (double z = minZ; z <= maxZ; z += distance) {
                        int components = 0;
                        if (x == minX || x == maxX) components++;
                        if (y == minY || y == maxY) components++;
                        if (z == minZ || z == maxZ) components++;
                        if (components >= 2) {
                            Location add = new Location(loc.getWorld(), x, y, z);
                            if (result.contains(add)) {
                                result.remove(add);
                            } else {
                                result.add(add);
                            }
                        }
                    }
                }
            }
        }
        Particle.DustOptions dust = new Particle.DustOptions(color, 0.7f);
        for (Location loc : result) {
            player.spawnParticle(Particle.REDSTONE, loc, 0, 0, 0, 0, dust);
        }
    }

    public static void attemptLock(Block block, Player player) {

        // Check If Block Is Lockable
        if (!isLockable(block)) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            new MessageManager("lock-invalid").send(player);
            return;
        }

        // Check If Player Placed Block
        String placer = getPlacer(block);
        if (!(placer == null && (Main.config.getBoolean("settings.allow-unplaced-locking") || EntityManager.hasScoreboardTag(player, "bypass-placed-locking")))) {
            if (placer == null || !placer.equals(player.getUniqueId().toString())) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                new MessageManager("lock-fail")
                        .replace("%block%", getName(block))
                        .replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(getPlacer(block)))).getName())
                        .send(player);
                return;
            }
        } else {
            place(block, player);
        }

        // Check If Block Is Already Locked
        if (isLocked(block)) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            new MessageManager("lock-again").send(player);
            return;
        }

        if (!attemptLockDoubleChest(block, player)) {
            lock(block, player);
        }
        new MessageManager("lock-success")
                .replace("%block%", getName(block))
                .send(player);
    }

    public static void lock(Block block, Player player) {
        Main.locked.set(getId(block) + ".locked", player.getUniqueId().toString());
        ConfigManager.saveData("locked.yml", Main.locked);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 2);
    }

    public static boolean attemptLockDoubleChest(Block block, Player player) {
        if (block.getState() instanceof Chest chest) {
            if (chest.getInventory() instanceof DoubleChestInventory doubleChest) {
                Location right = doubleChest.getRightSide().getLocation();
                Location left = doubleChest.getLeftSide().getLocation();
                for (Location location : new Location[]{right, left}) {
                    Block locationBlock = location.getBlock();
                    Main.locked.set(getId(locationBlock), null);
                    place(locationBlock, player);
                    lock(locationBlock, player);
                }
                return true;
            }
        }
        return false;
    }

    public static void checkLockDoubleChest(Block block, Player player) {
        if (block.getState() instanceof Chest chest) {
            if (chest.getInventory() instanceof DoubleChestInventory doubleChest) {
                Location right = doubleChest.getRightSide().getLocation();
                Location left = doubleChest.getLeftSide().getLocation();
                Location[] locations = {right, left};
                for (Location loc : locations) {
                    Block locBlock = loc.getBlock();
                    if (isLocked(locBlock)) {
                        String owner = getLocker(locBlock);
                        Main.locked.set(getId(locations[0].getBlock()) + ".locked", owner);
                        Main.locked.set(getId(locations[1].getBlock()) + ".locked", owner);
                        Main.locked.set(getId(locations[0].getBlock()) + ".place", owner);
                        Main.locked.set(getId(locations[1].getBlock()) + ".place", owner);
                        ConfigManager.saveData("locked.yml", Main.locked);
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 2);
                        return;
                    }
                }
            }
        }
    }

    public static void attemptUnlock(Block block, Player player) {

        // Check If Block Is Locked
        if (!isLocked(block)) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            new MessageManager("unlock-invalid").send(player);
            return;
        }

        // Check If Player Placed Block
        String placer = getPlacer(block);
        if ((placer == null || !placer.equals(player.getUniqueId().toString())) && !TeamManager.isStaff(player) && !player.isOp()) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
            new MessageManager("unlock-fail")
                    .replace("%block%", getName(block))
                    .replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(getPlacer(block)))).getName())
                    .send(player);
            return;
        }

        if (!attemptUnlockDoubleChest(block, player)) {
            unlock(block, player);
        }
        new MessageManager("unlock-success")
                .replace("%block%", getName(block))
                .send(player);
    }

    public static void unlock(Block block, Player player) {
        Main.locked.set(getId(block) + ".locked", null);
        ConfigManager.saveData("locked.yml", Main.locked);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 2);
    }

    public static boolean attemptUnlockDoubleChest(Block block, Player player) {
        if (block.getState() instanceof Chest chest) {
            if (chest.getInventory() instanceof DoubleChestInventory doubleChest) {
                Location right = doubleChest.getRightSide().getLocation();
                Location left = doubleChest.getLeftSide().getLocation();
                for (Location location : new Location[]{right, left}) {
                    Block locationBlock = location.getBlock();
                    unlock(locationBlock, player);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isTrustedBy(Player player, OfflinePlayer op) {
        List<String> trusted = Main.trusted.getStringList(op.getUniqueId().toString());
        return trusted.contains(player.getUniqueId().toString());
    }

    public static void trust(OfflinePlayer op, Player player) {
        List<String> trusted = Main.trusted.getStringList(player.getUniqueId().toString());
        if (!trusted.contains(op.getUniqueId().toString()) && op.getUniqueId() != player.getUniqueId()) {
            trusted.add(op.getUniqueId().toString());
            Main.trusted.set(player.getUniqueId().toString(), trusted);
            ConfigManager.saveData("trusted.yml", Main.trusted);
            new MessageManager("trust-success")
                    .replace("%player%", op.getName())
                    .send(player);
        } else {
            new MessageManager("trust-fail")
                    .replace("%player%", op.getName())
                    .send(player);
        }
    }

    public static void untrust(OfflinePlayer op, Player player) {
        List<String> trusted = Main.trusted.getStringList(player.getUniqueId().toString());
        if (trusted.contains(op.getUniqueId().toString())) {
            trusted.remove(op.getUniqueId().toString());
            Main.trusted.set(player.getUniqueId().toString(), trusted);
            ConfigManager.saveData("trusted.yml", Main.trusted);
            new MessageManager("untrust-success")
                    .replace("%player%", op.getName())
                    .send(player);
        } else {
            new MessageManager("untrust-fail")
                    .replace("%player%", op.getName())
                    .send(player);
        }
    }

    public static boolean isPlaced(Block block) {
        return Main.locked.contains(getId(block));
    }

    public static String getPlacer(Block block) {
        String id = getId(block);
        if (Main.locked.contains(id + ".place")) {
            return Main.locked.getString(id + ".place");
        }
        return null;
    }

    public static void place(Block block, Player player) {
        Main.locked.set(getId(block) + ".place", player.getUniqueId().toString());
        ConfigManager.saveData("locked.yml", Main.locked);
    }

    public static void unplace(Block block) {
        Main.locked.set(getId(block), null);
        ConfigManager.saveData("locked.yml", Main.locked);
    }

    public static String getId(Block block) {
        return block.getWorld().getName() + "=" + block.getX() + "=" + block.getY() + "=" + block.getZ() + "=" + block.getType().name();
    }

    public static String getName(Block block) {
        return WordUtils.capitalizeFully(block.getType().name().toLowerCase().replaceAll("_", " "));
    }

    public static ItemStack getLockTool() {
        return new ItemManager(Material.AMETHYST_SHARD)
                .name("&dLock Tool")
                .lore("&7Left Click &8= &7Lock")
                .lore("&7Right Click &8= &7Unlock")
                .enchant(Enchantment.MENDING, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .get();
    }

    public static boolean holdingLockTool(Player player) {
        return player.getInventory().getItemInMainHand().isSimilar(lockTool);
    }

    public static void registerRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.minecraft("lock_tool"), lockTool);
        recipe.shape("  B", " A ", "A  ");
        recipe.setIngredient('A', Material.STICK);
        recipe.setIngredient('B', Material.AMETHYST_SHARD);
        Bukkit.getServer().addRecipe(recipe);

    }

    public static void check() {
        for (String key : Main.locked.getKeys(false)) {
            String[] c = key.split("=");
            Location loc = new Location(Bukkit.getWorld(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2]), Double.parseDouble(c[3]));
            if (!loc.getBlock().getType().name().equalsIgnoreCase(c[4])) {
                Main.locked.set(key, null);
            }
        }
        ConfigManager.saveData("locked.yml", Main.locked);
    }
}
