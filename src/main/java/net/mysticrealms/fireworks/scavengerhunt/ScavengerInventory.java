package net.mysticrealms.fireworks.scavengerhunt;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ScavengerInventory implements Runnable {

	private ScavengerHunt plugin;

	public ScavengerInventory(ScavengerHunt scavenger) {
		plugin = scavenger;
	}

	@Override
	public void run() {
		if (plugin.end != 0 && plugin.end < System.currentTimeMillis()) {
			plugin.stopScavengerEvent();
			return;
		}
		for (Player p : plugin.getServer().getOnlinePlayers()) {

			if (!p.hasPermission("scavengerhunt.participate")) {
				continue;
			}

			Inventory i = p.getInventory();
			boolean doneObjectives = true;
			for (ItemStack item : plugin.currentItems) {
				if (plugin.count(i, item) < item.getAmount()) {
					doneObjectives = false;
				}
			}
			Map<EntityType, Integer> usedEntities = plugin.getMap(p.getName());
			for (Map.Entry<EntityType, Integer> entry : plugin.currentMobs.entrySet()) {
				if (entry.getValue() > usedEntities.get(entry.getKey())) {
					doneObjectives = false;
				}
			}
			if (doneObjectives) {
				plugin.isRunning = false;
				plugin.messager.sendAll(ChatColor.DARK_RED + "Congratulations to " + ChatColor.GOLD + p.getDisplayName() + ChatColor.DARK_RED + "!");
				if (plugin.removeItems) {
					ScavengerHandler.takeItems(plugin, p);
				}
				ScavengerHandler.giveRewards(plugin, p);
				plugin.getServer().getScheduler().cancelTask(plugin.taskId);
				return;
			}
		}
	}
}
