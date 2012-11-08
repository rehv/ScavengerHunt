package net.mysticrealms.fireworks.scavengerhunt;

import java.util.HashMap;

import net.mysticrealms.fireworks.scavengerhunt.util.ExperienceUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ScavengerHandler {
<<<<<<< HEAD
    public static void takeItems(ScavengerHunt plugin, Player p) {
        Inventory i = p.getInventory();

        for (ItemStack item : plugin.currentItems) {
            i.remove(item);
        }
    }

    public static void giveRewards(ScavengerHunt plugin, Player p) {
        Inventory i = p.getInventory();

        plugin.messager.sendAll(ChatColor.DARK_RED + "Prize was: ");
        for (ItemStack reward : plugin.currentRewards) {
            plugin.messager.sendAll(ChatColor.GOLD + plugin.messager.configToString(reward));

            HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
            leftOver.putAll((i.addItem(reward)));
            if (!leftOver.isEmpty()) {
                p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.getMaterial(leftOver.get(0).getTypeId()), leftOver.get(0).getAmount()));
            }
        }
        if (plugin.isUsingMoney()) {
            plugin.messager.sendAll(ChatColor.GOLD + " * " + ScavengerHunt.economy.format(plugin.moneyReward));
            ScavengerHunt.economy.depositPlayer(p.getName(), plugin.moneyReward);
        }
        if (plugin.isUsingExp()) {
            plugin.messager.sendAll(ChatColor.GOLD + " * " + plugin.expReward + " exp");
            ExperienceUtils.changeExp(p, plugin.expReward);
        }
        return;
    }
=======
	public static void takeItems(ScavengerHunt plugin, Player p) {
		Inventory i = p.getInventory();

		for (ItemStack item : plugin.currentItems) {
			i.remove(item);
		}
	}

	public static void giveRewards(ScavengerHunt plugin, Player p) {
		Inventory i = p.getInventory();

		plugin.messager.sendAll(ChatColor.DARK_RED + "Prize was: ");
		for (ItemStack reward : plugin.currentRewards) {
			plugin.messager.sendAll(ChatColor.GOLD + plugin.messager.configToString(reward));
			
			HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
			leftOver.putAll((i.addItem(reward)));
			if (!leftOver.isEmpty()) {
				p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.getMaterial(leftOver.get(0).getTypeId()), leftOver.get(0).getAmount()));
			}
		}
		if (plugin.isUsingMoney()) {
			plugin.messager.sendAll(ChatColor.GOLD + " * " + ScavengerHunt.economy.format(plugin.moneyReward));
			ScavengerHunt.economy.depositPlayer(p.getName(), plugin.moneyReward);
		}
		if (plugin.isUsingExp()) {
			plugin.messager.sendAll(ChatColor.GOLD + " * " + plugin.expReward + " exp");
			ExperienceUtils.changeExp(p, plugin.expReward);
		}
		return;
	}
>>>>>>> refs/remotes/origin/master
}
