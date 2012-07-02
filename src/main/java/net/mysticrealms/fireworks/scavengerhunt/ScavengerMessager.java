package net.mysticrealms.fireworks.scavengerhunt;

import java.util.Map;

import net.mysticrealms.fireworks.scavengerhunt.util.PotionRetriever;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.material.Wool;

public class ScavengerMessager {
	ScavengerHunt plugin;

	public boolean shortMessages, riddleMode, existItems, existMobs;

	public ScavengerMessager(ScavengerHunt scavenger) {
		plugin = scavenger;

		shortMessages = plugin.shortMessages;
		riddleMode = plugin.riddleMode;
		existItems = !plugin.currentItems.isEmpty();
		existMobs = !plugin.currentMobs.isEmpty();
	}

	public void displayStart() {
		existItems = !plugin.currentItems.isEmpty();
		existMobs = !plugin.currentMobs.isEmpty();
		sendAll(ChatColor.DARK_RED + "Scavenger Hunt is starting! Good luck!");
		if (plugin.duration > 0) {
			sendAll(ChatColor.DARK_RED + "You have: " + ChatColor.GOLD + timeFormatter(plugin.duration) + "!");
		}

		if ((existItems || existMobs) && shortMessages) {
			sendAll(ChatColor.GOLD + "/scavengerItems" + ChatColor.DARK_RED + " to view objectives.");
		}

		if ((existItems || existMobs) && !shortMessages && riddleMode) {
			sendAll(ChatColor.DARK_RED + "Here are the clues: ");
			for (String i : plugin.riddles) {
				sendAll(ChatColor.GOLD + " * " + i);
			}
		}

		if (existItems && !shortMessages && !riddleMode) {
			sendAll(ChatColor.DARK_RED + "You need to collect: ");
			for (ItemStack i : plugin.currentItems) {
				sendAll(ChatColor.GOLD + configToString(i));
			}
		}

		if (existMobs && !shortMessages && !riddleMode) {
			sendAll(ChatColor.DARK_RED + "You need to kill: ");
			for (Map.Entry<EntityType, Integer> entry : plugin.currentMobs.entrySet()) {
				sendAll(ChatColor.GOLD + " * " + entry.getValue() + " " + entry.getKey().getName().toLowerCase().replace("_", " "));
			}
		}
		sendAll(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerRewards" + ChatColor.DARK_RED + " to view rewards.");
	}

	public void listHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_RED + "== Scavenger Help Guide ==");
		sender.sendMessage(ChatColor.GOLD + " * /scavengerItems - List objectives for current scavenger event.");
		sender.sendMessage(ChatColor.GOLD + " * /scavengerRewards - List rewards for the winner.");
		if (sender.hasPermission("scavengerhunt.start")) {
			sender.sendMessage(ChatColor.GOLD + " * /scavengerStart - Start a scavenger event.");
		}
		if (sender.hasPermission("scavengerhunt.stop")) {
			sender.sendMessage(ChatColor.GOLD + " * /scavengerStop - End current scavenger vent.");
		}
		if (sender.hasPermission("scavengerhunt.reload")) {
			sender.sendMessage(ChatColor.GOLD + " * /scavengerReload - Reload the config.");
		}
	}

	public void listScavengerEventItems(CommandSender sender) {
		if (!(sender instanceof Player)) {
			return;
		}
		Player p = (Player) sender;
		if (plugin.isRunning) {
			if ((existItems || existMobs) && riddleMode) {
				sender.sendMessage(ChatColor.DARK_RED + "Current scavenger clues: ");
				for (String i : plugin.riddles) {
					sender.sendMessage(ChatColor.GOLD + i);
				}
			} else {
				if (existItems) {
					sender.sendMessage(ChatColor.DARK_RED + "Current scavenger item objectives: ");
					for (ItemStack i : plugin.currentItems) {
						sender.sendMessage(ChatColor.GOLD + configToString(i, plugin.count(p.getInventory(), i)));
					}
				}
				if (existMobs) {
					sender.sendMessage(ChatColor.DARK_RED + "Current scavenger mob objectives: ");
					Map<EntityType, Integer> status = plugin.getMap(sender.getName());
					for (Map.Entry<EntityType, Integer> entry : plugin.currentMobs.entrySet()) {
						sender.sendMessage(ChatColor.GOLD + " * " + status.get(entry.getKey()) + "/" + entry.getValue() + " " + entry.getKey().getName().toLowerCase().replace("_", " "));
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "No scavenger event is currently running.");
		}
	}

	public void listScavengerEventRewards(CommandSender sender) {
		if (plugin.isRunning) {
			sender.sendMessage(ChatColor.DARK_RED + "Current scavenger rewards: ");
			for (ItemStack i : plugin.currentRewards) {
				sender.sendMessage(ChatColor.GOLD + configToString(i));
			}
			if (plugin.isUsingMoney()) {
				sender.sendMessage(ChatColor.GOLD + " * " + ScavengerHunt.economy.format(plugin.moneyReward));
			}
			if (plugin.isUsingExp()) {
				sender.sendMessage(ChatColor.GOLD + " * " + plugin.expReward + " exp");
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "No scavenger event is currently running.");
		}
	}

	public void sendAll(String message) {
		plugin.getServer().broadcastMessage(message);
	}

	public String itemFormatter(ItemStack item) {
		if (item.getType() == Material.WOOL) {
			return ((Wool) item.getData()).getColor().toString() + " wool";
		} else if (item.getType() == Material.INK_SACK) {
			return ((Dye) item.getData()).getColor().toString() + " dye";
		} else if (item.getType() == Material.POTION) {
			return PotionRetriever.getPotionName(item.getDurability());
		} else {
			return item.getType().toString();
		}
	}

	public String configToString(ItemStack item) {
		return configToString(item, -1);
	}

	public String configToString(ItemStack item, int current) {
		String itemName = itemFormatter(item).toLowerCase().replace("_", " ").replace("ii", "II");
		return " * " + ((current != -1) ? current + "/" : "") + item.getAmount() + " " + itemName;
	}

	private String timeFormatter(int duration) {
		int step = duration;
		StringBuilder formatter = new StringBuilder();

		if (step / 3600 > 0) {
			formatter.append(String.format("%02d", step / 3600));
			formatter.append("h");
			step = step % 3600;
		}
		if (step / 60 > 0) {
			formatter.append(String.format("%02d", step / 60));
			formatter.append("m");
			step = step % 60;
		}
		if (step > 0) {
			formatter.append(String.format("%02d", step));
			formatter.append("s");
		}

		return formatter.toString();
	}
}
