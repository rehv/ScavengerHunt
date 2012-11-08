package net.mysticrealms.fireworks.scavengerhunt;

import it.sauronsoftware.cron4j.InvalidPatternException;
import it.sauronsoftware.cron4j.Scheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.economy.Economy;
import net.mysticrealms.fireworks.scavengerhunt.util.ItemUtils;
import net.mysticrealms.fireworks.scavengerhunt.util.NameRetriever;

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

    public List<String> playerList = new ArrayList<String>();

    public int taskId = -1;
    public List<EntityType> riddleMobList = new ArrayList<EntityType>();
    public List<String> itemRiddles = new ArrayList<String>();
    public List<String> mobRiddles = new ArrayList<String>();
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
        if (economy != null && moneyReward > 0) {
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
        mobRiddles.clear();
        itemRiddles.clear();
        riddleMobList.clear();

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

        if (config.isList("itemRiddles")) {
            for (String i : config.getStringList("itemRiddles")) {
                itemRiddles.add(i.toString());
            }
        }

        if (config.isList("mobRiddles")) {
            for (String i : config.getStringList("mobRiddles")) {
                mobRiddles.add(i.toString());
            }
        }

        if (config.isList("mobs")) {
            for (Object i : config.getList("mobs", new ArrayList<String>())) {
                try {
                    final String[] parts = i.toString().split(" ");
                    final int mobQuantity = Integer.parseInt(parts[1]);
                    final EntityType mobName = EntityType.fromName(parts[0]);
                    mobs.put(mobName, mobQuantity);
                    riddleMobList.add(mobName);
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
                    ItemStack result = ItemUtils.parseItem(i.toString());
                    if (result != null) {
                        items.add(result);
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
                    ItemStack result = ItemUtils.parseItem(i.toString());
                    if (result != null) {
                        rewards.add(result);
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
        } else if (cmd.getName().equalsIgnoreCase("scavengerstop")) {
            if (sender.hasPermission("scavengerhunt.stop")) {
                if (isRunning) {
                    stopScavengerEvent();
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "No scavenger event is currently running.");
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("scavengeritems")) {
            if (sender.hasPermission("scavengerhunt.items")) {
                messager.listScavengerEventItems(sender);
            }
        } else if (cmd.getName().equalsIgnoreCase("scavengerrewards")) {
            if (sender.hasPermission("scavengerhunt.rewards")) {
                messager.listScavengerEventRewards(sender);
            }
        } else if (cmd.getName().equalsIgnoreCase("scavengerhelp")) {
            messager.listHelp(sender);
        } else if (cmd.getName().equalsIgnoreCase("scavengerreload")) {
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
        } else if (cmd.getName().equalsIgnoreCase("scavengerjoin")) {
            if (isRunning) {
                if (sender.hasPermission("scavengerhunt.participate")) {
                    if (sender instanceof Player) {
                        Player p = ((Player) sender).getPlayer();
                        playerList.add(p.getDisplayName());
                        messager.displayJoin(p);
                    } else {
                        getLogger().info("This command can only be issued by players!");
                    }
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
        NameRetriever.setEnchantMap();
        ScavengerSaver.plugin = this;
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
        if (isRunning) {
            stopScavengerEvent();
        }
        currentItems.clear();
        playerMobs.clear();
        currentMobs.clear();
        currentRewards.clear();
        riddles.clear();
        playerList.clear();

        // saver
        ScavengerSaver.newScavengerID();

        moneyReward = moneyBackup;
        expReward = expBackup;

        if (riddleMode) {
            if (mobRiddles.size() != mobs.size()) {
                getLogger().info("Number of mobRiddles and mobs listed mismatch. Cannot use riddleMode.");
                riddleMode = false;
            }
            if (itemRiddles.size() != items.size()) {
                getLogger().info("Number of itemRiddles and items listed mismatch. Cannot use riddleMode.");
                riddleMode = false;
            }
        }

        Random r = new Random();
        if (globalNumOfObjectives >= 0) {
            numOfItems = 0;
            numOfMobs = 0;
        }
        if (globalNumOfObjectives > 0) {
            for (int i = globalNumOfObjectives; i > 0; i--) {
                if (r.nextInt() % 2 == 0) {
                    if ((numOfItems + 1) <= items.size()) {
                        numOfItems++;
                    } else if ((numOfMobs + 1) <= mobs.size()) {
                        numOfMobs++;
                    }
                } else {
                    if ((numOfMobs + 1) <= mobs.size()) {
                        numOfMobs++;
                    } else if ((numOfItems + 1) <= items.size()) {
                        numOfItems++;
                    }
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
                ItemStack entry = clone.remove(r.nextInt(clone.size()));
                currentItems.add(entry);
                if (riddleMode) {
                    int index = items.indexOf(entry);
                    riddles.add(itemRiddles.get(index));
                }
            }
        }

        List<Map.Entry<EntityType, Integer>> mobClone = new ArrayList<Map.Entry<EntityType, Integer>>(mobs.entrySet());
        for (int i = 0; (numOfMobs < 0 || i < numOfMobs) && !mobClone.isEmpty(); i++) {
            Map.Entry<EntityType, Integer> entry = mobClone.remove(r.nextInt(mobClone.size()));
            currentMobs.put(entry.getKey(), entry.getValue());
            if (riddleMode) {
                int index = riddleMobList.indexOf(entry.getKey());
                riddles.add(mobRiddles.get(index));
            }
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
