package net.mysticrealms.fireworks.scavengerhunt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.economy.Economy;

import it.sauronsoftware.cron4j.InvalidPatternException;
import it.sauronsoftware.cron4j.Scheduler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.mysticrealms.fireworks.scavengerhunt.util.NameRetriever;

public class ScavengerHunt extends JavaPlugin {
	public ScavengerMessager messager;
	public static Economy economy = null;
	public Configuration config;
	public int duration = 0;
	public long end = 0;

	public boolean isRunning, usingScheduler, shortMessages, riddleMode, removeItems, enableMetrics;

	public double moneyReward = 0;
	public double moneyBackup = 0;
	public int expReward = 0;
	public int expBackup = 0;

	public Map<String, Map<EntityType, Integer>> playerMobs = new ConcurrentHashMap<String, Map<EntityType, Integer>>();

	public List<ItemStack> items = new ArrayList<ItemStack>();
	public Map<EntityType, Integer> mobs = new HashMap<EntityType, Integer>();
	public List<ItemStack> rewards = new ArrayList<ItemStack>();

	public List<ItemStack> currentItems = new ArrayList<ItemStack>();
	public Map<EntityType, Integer> currentMobs = new HashMap<EntityType, Integer>();
	public List<ItemStack> currentRewards = new ArrayList<ItemStack>();

	public int numOfItems = 0;
	public int numOfMobs = 0;
	public int numOfRewards = 0;
	public int globalNumOfObjectives = 0;
	public int globalNumOfRewards = 0;

	public Scheduler scheduler = new Scheduler();
	public String schedule = "";
	public String task;

	public int taskId = -1;
	public List<String> riddles = new ArrayList<String>();

	public synchronized Map<EntityType, Integer> getMap(String s) {
		Map<EntityType, Integer> map = playerMobs.get(s);
		if (map == null) {
			map = new ConcurrentHashMap<EntityType, Integer>();
			for (EntityType e : EntityType.values()) {
				map.put(e, 0);
			}
			playerMobs.put(s, map);
		}
		return map;
	}

	public boolean isUsingMoney() {
		if (moneyReward > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isUsingExp() {
		if (expReward > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean loadConfig() {

		items.clear();
		rewards.clear();
		mobs.clear();
		riddles.clear();

		if (task != null) {
			scheduler.deschedule(task);
			task = null;
		}

		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}

		reloadConfig();
		config = getConfig();

		if (config.isBoolean("riddleMode")) {
			riddleMode = config.getBoolean("riddleMode");
		}

		if (config.isBoolean("shortMessageMode")) {
			shortMessages = config.getBoolean("shortMessageMode");
		}

		if (config.isBoolean("enableScheduler")) {
			usingScheduler = config.getBoolean("enableScheduler");
		}

		if (config.isString("schedule") && usingScheduler) {
			schedule = config.getString("schedule");
			try {
				task = scheduler.schedule(schedule, new Runnable() {

					@Override
					public void run() {
						runScavengerEvent();

					}
				});
			} catch (InvalidPatternException e) {
				getLogger().warning("Scheduler string is wrong! Automatic start disabled.");
			}
		}

		if (config.isList("riddles")) {
			for (String i : config.getStringList("riddles")) {
				riddles.add(i.toString());
			}
		}

		if (config.isList("mobs")) {
			for (Object i : config.getList("mobs", new ArrayList<String>())) {
				try {
					final String[] parts = i.toString().split(" ");
					final int mobQuantity = Integer.parseInt(parts[1]);
					final EntityType mobName = EntityType.fromName(parts[0]);
					mobs.put(mobName, mobQuantity);
				} catch (Exception e) {
					return false;
				}
			}
		}
		if (config.isDouble("moneyReward")) {
			moneyReward = config.getDouble("moneyReward");
		} else if (config.isInt("moneyReward")) {
			moneyReward = config.getInt("moneyReward");
		} else {
			return false;
		}
		moneyBackup = moneyReward;
		if (config.isInt("expReward")) {
			expReward = config.getInt("expReward");
		} else {
			return false;
		}
		expBackup = expReward;
		if (config.isInt("duration")) {
			duration = config.getInt("duration");
		} else {
			return false;
		}
		if (config.isInt("numOfItems")) {
			numOfItems = config.getInt("numOfItems");
		} else {
			return false;
		}
		if (config.isInt("numOfMobs")) {
			numOfMobs = config.getInt("numOfMobs");
		} else {
			return false;
		}
		if (config.isInt("numOfRewards")) {
			numOfRewards = config.getInt("numOfRewards");
		} else {
			return false;
		}
		if (config.isInt("globalNumOfObjectives")) {
			globalNumOfObjectives = config.getInt("globalNumOfObjectives");
		} else {
			return false;
		}
		if (config.isInt("globalNumOfRewards")) {
			globalNumOfRewards = config.getInt("globalNumOfRewards");
		} else {
			return false;
		}
		if (config.isList("items")) {
			for (Object i : config.getList("items", new ArrayList<String>())) {
				if (i instanceof String) {
					final String[] parts = ((String) i).split(" ");
					final int[] intParts = new int[parts.length];
					for (int e = 0; e < parts.length; e++) {
						try {
							intParts[e] = Integer.parseInt(parts[e]);
						} catch (final NumberFormatException exception) {
							return false;
						}
					}
					if (parts.length == 1) {
						items.add(new ItemStack(intParts[0], 1));
					} else if (parts.length == 2) {
						items.add(new ItemStack(intParts[0], intParts[1]));
					} else if (parts.length == 3) {
						items.add(new ItemStack(intParts[0], intParts[1], (short) intParts[2]));
					}
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		if (config.isList("rewards")) {
			for (Object i : config.getList("rewards", new ArrayList<String>())) {
				if (i instanceof String) {
					final String[] parts = ((String) i).split(" ");
					final int[] intParts = new int[parts.length];
					for (int e = 0; e < parts.length; e++) {
						try {
							intParts[e] = Integer.parseInt(parts[e]);
						} catch (final NumberFormatException exception) {
							return false;
						}
					}
					if (parts.length == 1) {
						rewards.add(new ItemStack(intParts[0], 1));
					} else if (parts.length == 2) {
						rewards.add(new ItemStack(intParts[0], intParts[1]));
					} else if (parts.length == 3) {
						rewards.add(new ItemStack(intParts[0], intParts[1], (short) intParts[2]));
					}
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		if (config.isBoolean("removeItems")) {
			removeItems = config.getBoolean("removeItems");
		} else {
			removeItems = false;
		}
		if (config.isBoolean("enableMetrics")) {
			enableMetrics = config.getBoolean("enableMetrics");
		} else {
			enableMetrics = true;
		}
		messager = new ScavengerMessager(this);
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("scavengerstart")) {
			if (sender.hasPermission("scavengerhunt.start")) {
				runScavengerEvent();
			}
		}
		if (cmd.getName().equalsIgnoreCase("scavengerstop")) {
			if (sender.hasPermission("scavengerhunt.stop")) {
				if (isRunning) {
					stopScavengerEvent();
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "No scavenger event is currently running.");
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("scavengeritems")) {
			if (sender.hasPermission("scavengerhunt.items")) {
				messager.listScavengerEventItems(sender);
			}
		}
		if (cmd.getName().equalsIgnoreCase("scavengerrewards")) {
			if (sender.hasPermission("scavengerhunt.rewards")) {
				messager.listScavengerEventRewards(sender);
			}
		}
		if (cmd.getName().equalsIgnoreCase("scavengerhelp")) {
			messager.listHelp(sender);
		}
		if (cmd.getName().equalsIgnoreCase("scavengerreload")) {
			if (sender.hasPermission("scavengerhunt.reload")) {
				if (isRunning) {
					stopScavengerEvent();
				}
				if (loadConfig()) {
					sender.sendMessage(ChatColor.GOLD + "Config reloaded!");
				} else {
					sender.sendMessage(ChatColor.GOLD + "Config failed to reload!");
				}
			}
		}
		return true;
	}

	@Override
	public void onEnable() {

		scheduler.start();
		if (enableMetrics) {
			startMetrics();
		}
		setupEconomy();
		NameRetriever.setPotionMap();
		NameRetriever.setDiscMap();
		if (!loadConfig()) {
			getLogger().severe("Something is wrong with the config! Disabling!");
			setEnabled(false);
			return;
		}

		getServer().getPluginManager().registerEvents(new ScavengerListener(this), this);
	}

	public void onDisable() {
		scheduler.stop();
	}

	public int count(Inventory inv, ItemStack item) {
		int count = 0;
		for (ItemStack check : inv.getContents()) {
			if (check != null && check.getType() == item.getType() && check.getData().equals(item.getData())) {
				count += check.getAmount();
			}
		}
		return count;
	}

	public void runScavengerEvent() {
		currentItems.clear();
		playerMobs.clear();
		currentMobs.clear();
		currentRewards.clear();

		moneyReward = moneyBackup;
		expReward = expBackup;

		// disabled for this version
		riddleMode = false;

		Random r = new Random();
		if (globalNumOfObjectives >= 0) {
			numOfItems = 0;
			numOfMobs = 0;
		}
		if (globalNumOfObjectives > 0) {
			for (int i = globalNumOfObjectives; i > 0; i--) {
				if (r.nextInt() % 2 == 0) {
					numOfItems++;
				} else {
					numOfMobs++;
				}
			}
		}

		if (globalNumOfRewards > 0) {
			numOfRewards = globalNumOfRewards;
			boolean randomReward[] = new boolean[] { false, false };
			int size = rewards.size();
			for (int i = globalNumOfRewards; i > 0; i--) {
				if (r.nextInt(size + 2) % (size) == 0 && !randomReward[0]) {
					randomReward[0] = true;
					numOfRewards--;
				}
				if (r.nextInt(size + 2) % (size + 1) == 0 && !randomReward[1]) {
					randomReward[1] = true;
					numOfRewards--;
				}
			}
			if (!randomReward[0]) {
				moneyReward = 0;
			}
			if (!randomReward[1]) {
				expReward = 0;
			}
		}

		List<ItemStack> rewardClone = new ArrayList<ItemStack>();
		for (ItemStack i : rewards) {
			rewardClone.add(i);
		}
		if (numOfRewards <= 0) {
			currentRewards = rewardClone;
		} else {
			for (int i = 0; i < numOfRewards && !rewardClone.isEmpty(); i++) {
				currentRewards.add(rewardClone.remove(r.nextInt(rewardClone.size())));
			}
		}

		if (numOfItems == 0 && numOfMobs == 0) {
			messager.sendAll(ChatColor.DARK_RED + "Scavenger hunt free mode! Everyone gets the rewards!");
			for (Player p : getServer().getOnlinePlayers()) {
				ScavengerHandler.giveRewards(this, p);
			}
			return;
		}

		List<ItemStack> clone = new ArrayList<ItemStack>();
		for (ItemStack i : items) {
			clone.add(i);
		}
		if (numOfItems < 0) {
			currentItems = clone;
		} else {
			for (int i = 0; i < numOfItems && !clone.isEmpty(); i++) {
				currentItems.add(clone.remove(r.nextInt(clone.size())));
			}
		}

		List<Map.Entry<EntityType, Integer>> mobClone = new ArrayList<Map.Entry<EntityType, Integer>>(mobs.entrySet());
		for (int i = 0; (numOfMobs < 0 || i < numOfMobs) && !mobClone.isEmpty(); i++) {
			Map.Entry<EntityType, Integer> entry = mobClone.remove(r.nextInt(mobClone.size()));
			currentMobs.put(entry.getKey(), entry.getValue());
		}

		messager.displayStart();

		taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new ScavengerInventory(this), 0, 40);
		if (taskId != -1) {
			isRunning = true;
		}

		if (duration == 0) {
			end = 0;
		} else {
			end = System.currentTimeMillis() + duration * 1000;
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			getLogger().info("Vault found and loaded.");
			return economy != null;
		}
		economy = null;
		getLogger().info("Vault not found - money reward will not be used.");
		return false;
	}

	public void startMetrics() {
		try {
			new MetricsLite(this).start();
		} catch (IOException e) {
			getLogger().warning("MetricsLite did not enable! Statistic usage disabled.");
			e.printStackTrace();
			return;
		}
	}

	public void stopScavengerEvent() {
		messager.sendAll(ChatColor.DARK_RED + "Scavenger Hunt has ended with no winner.");
		getServer().getScheduler().cancelTask(taskId);
		isRunning = false;
	}
}
