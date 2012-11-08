package net.mysticrealms.fireworks.scavengerhunt;

import java.util.Map;

import net.mysticrealms.fireworks.scavengerhunt.util.NameRetriever;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
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
            sendAll(ChatColor.DARK_RED + "This event will last for: " + ChatColor.GOLD + timeFormatter(plugin.duration) + "!");
        }
        sendAll(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerJoin" + ChatColor.DARK_RED + " to join the event!");

        if ((existItems || existMobs)) {
            sendAll(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerItems" + ChatColor.DARK_RED + " to view objectives.");
        }
        sendAll(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerRewards" + ChatColor.DARK_RED + " to view rewards.");
    }

    public void displayJoin(Player p) {
        existItems = !plugin.currentItems.isEmpty();
        existMobs = !plugin.currentMobs.isEmpty();
        p.sendMessage(ChatColor.DARK_RED + "You have joined this Scavenger Hunt! Good luck!");
        long timeDiff = plugin.end - System.currentTimeMillis();
        String timeLeft = ScavengerMessager.timeFormatter((int) ((timeDiff) / 1000));
        if (plugin.duration > 0) {
            p.sendMessage(ChatColor.DARK_RED + "You have: " + ChatColor.GOLD + timeLeft + "!");
        }

        if ((existItems || existMobs) && shortMessages) {
            p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerItems" + ChatColor.DARK_RED + " to view objectives.");
        }

        if ((existItems || existMobs) && !shortMessages && riddleMode) {
            p.sendMessage(ChatColor.DARK_RED + "Here are the clues: ");
            for (String i : plugin.riddles) {
                p.sendMessage(ChatColor.GOLD + " * " + i);
            }
        }

        if (existItems && !shortMessages && !riddleMode) {
            p.sendMessage(ChatColor.DARK_RED + "You need to collect: ");
            for (ItemStack i : plugin.currentItems) {
                p.sendMessage(ChatColor.GOLD + configToString(i));
            }
        }

        if (existMobs && !shortMessages && !riddleMode) {
            p.sendMessage(ChatColor.DARK_RED + "You need to kill: ");
            for (Map.Entry<EntityType, Integer> entry : plugin.currentMobs.entrySet()) {
                p.sendMessage(ChatColor.GOLD + " * " + entry.getValue() + " " + entry.getKey().getName().toLowerCase().replace("_", " "));
            }
        }
        p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerRewards" + ChatColor.DARK_RED + " to view rewards.");
    }

    public void displayJoinCurrent(Player p) {
        long timeDiff = plugin.end - System.currentTimeMillis();
        String timeLeft = timeFormatter((int) ((timeDiff) / 1000));

        p.sendMessage(ChatColor.DARK_RED + "There is a Scavenger Hunt happening!");
        if (plugin.duration > 0) {
            p.sendMessage(ChatColor.DARK_RED + "This event will last for: " + ChatColor.GOLD + timeLeft + "!");
        }
        p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerJoin" + ChatColor.DARK_RED + " to join the event!");
        p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerItems" + ChatColor.DARK_RED + " to view objectives.");
        p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerRewards" + ChatColor.DARK_RED + " to view rewards.");
    }

    public void displayRejoin(Player p) {
        long timeDiff = plugin.end - System.currentTimeMillis();
        String timeLeft = timeFormatter((int) ((timeDiff) / 1000));

        p.sendMessage(ChatColor.DARK_RED + "Your progress in the current scavenger was loaded!");
        if (plugin.duration > 0) {
            p.sendMessage(ChatColor.DARK_RED + "You still have: " + ChatColor.GOLD + timeLeft + "!");
        }
        p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerItems" + ChatColor.DARK_RED + " to view your current progress.");
        p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerRewards" + ChatColor.DARK_RED + " to view rewards.");
    }

<<<<<<< HEAD
    public void displayRejoinFailed(Player p) {
        p.sendMessage(ChatColor.DARK_RED + "Your progress in the current scavenger couldn't be loaded.");
        p.sendMessage(ChatColor.DARK_RED + "Your mob objectives will need to be done again.");
    }
=======
	public void displayRejoinFailed(Player p) {
		p.sendMessage(ChatColor.DARK_RED + "Your progress in the current scavenger couldn't be loaded.");
		p.sendMessage(ChatColor.DARK_RED + "Your mob objectives will need to be done again.");
	}
>>>>>>> refs/remotes/origin/master

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
            if (existItems) {
                sender.sendMessage(ChatColor.DARK_RED + "Current scavenger item objectives: ");
                for (ItemStack i : plugin.currentItems) {
                    sender.sendMessage(ChatColor.GOLD + configToString(i));
                }
            }

            if (existMobs) {
                sender.sendMessage(ChatColor.DARK_RED + "Current scavenger mob objectives: ");
                for (Map.Entry<EntityType, Integer> entry : plugin.currentMobs.entrySet()) {
                    sender.sendMessage(ChatColor.GOLD + " * " + entry.getValue() + " " + entry.getKey().getName().toLowerCase().replace("_", " "));
                }
            }
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
                        int mobProgress = status.get(entry.getKey());
                        if (mobProgress > entry.getValue()) {
                            mobProgress = entry.getValue();
                        }
                        sender.sendMessage(ChatColor.GOLD + " * " + mobProgress + "/" + entry.getValue() + " " + entry.getKey().getName().toLowerCase().replace("_", " "));
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
            return NameRetriever.getPotionName(item.getDurability());
        } else if (item.getType().isRecord()) {
            return NameRetriever.getDiscName(item.getTypeId());
        } else {
            Map<Enchantment, Integer> enchants = item.getEnchantments();
            if (enchants.isEmpty()) {
                return item.getType().toString();
            } else {
                String builder = item.getType().toString();
                builder += " (";
                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    builder += NameRetriever.getEnchantName(entry.getKey().getId());
                    switch (entry.getValue()) {
                    case 1:
                        builder += " I";
                        break;
                    case 2:
                        builder += " II";
                        break;
                    case 3:
                        builder += " III";
                        break;
                    case 4:
                        builder += " IV";
                        break;
                    case 5:
                        builder += " V";
                        break;
                    }
                    builder += " ";
                }
                builder = builder.trim();
                builder += ")";
                return builder;
            }
        }
    }

    public String configToString(ItemStack item) {
        return configToString(item, -1);
    }

    public String configToString(ItemStack item, int current) {
        String itemName = itemFormatter(item).toLowerCase().replace("_", " ").replace(" v", " V").replace(" iv", " IV").replace(" iii", " III").replace(" ii", " II").replace(" i", " I");
        return " * " + ((current != -1) ? current + "/" : "") + item.getAmount() + " " + itemName;
    }

    public static String timeFormatter(int duration) {
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
