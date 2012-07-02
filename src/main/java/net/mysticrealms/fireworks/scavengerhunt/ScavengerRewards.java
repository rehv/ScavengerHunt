package net.mysticrealms.fireworks.scavengerhunt;

import java.util.HashMap;

import net.mysticrealms.fireworks.scavengerhunt.util.ExperienceUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ScavengerRewards {
	public static void giveRewards(ScavengerHunt plugin, Player p) {
		Inventory i = p.getInventory();

		plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Prize was: ");
		for (ItemStack reward : plugin.currentRewards) {
			plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.messager.configToString(reward));

			HashMap<Integer, ItemStack> leftOver = new HashMap<Integer, ItemStack>();
			leftOver.putAll((i.addItem(reward)));
			if (!leftOver.isEmpty()) {
				p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.getMaterial(leftOver.get(0).getTypeId()), leftOver.get(0).getAmount()));
			}
		}
		if (plugin.isUsingMoney()) {
			plugin.getServer().broadcastMessage(ChatColor.GOLD + " * " + ScavengerHunt.economy.format(plugin.moneyReward));
			ScavengerHunt.economy.depositPlayer(p.getName(), plugin.moneyReward);
		}
		if (plugin.isUsingExp()) {
			ExperienceUtils.changeExp(p, plugin.expReward);
		}
		return;
	}
}
