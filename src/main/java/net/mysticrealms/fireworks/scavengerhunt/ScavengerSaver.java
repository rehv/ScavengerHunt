package net.mysticrealms.fireworks.scavengerhunt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * @author rehv@github.com - rf.rehv@gmail.com
 * 
 *         ideas:
 * 
 *         config.yml "allowSave:true/false". whenever progression is updated, it is saved. (needed only for mobs) YYYYMMDDHHMMSS - scavengerID, saved
 *         data only last for the same scavenger create PlayerName.yml, delete all yml when a new scavengerhunt starts (could be changed if allowed
 *         multiple scavengers). - deleting is a late feature. save all for a while. use Listener OnPlayerLogin, pull Player file, check scavengerID.
 *         RunScavengerEvent cria novo saver.
 */

public class ScavengerSaver {
    public static ScavengerHunt plugin;
    private static String scavengerID;

    /*
     * p/ Logger java.util.Date now = new Date(); //now.getTime() returns the Unix-Timestamp.
     * 
     * //format the output to "24-01-2012 15:19:45" SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
     * System.out.println("Time: "+format.format(now));
     */

    // change scavengerID to a list of scavengerID of all happening scavengers
    protected static void newScavengerID() {
        Date start = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        scavengerID = format.format(start);
    }

    protected static void savePlayer(Player p) {
        File playerData = new File(plugin.getDataFolder(), "savedData" + File.pathSeparator + p.getDisplayName() + ".yml");
        if (!playerData.exists()) {
            try {
                playerData.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().info("Could not create savedData file for player " + p.getDisplayName() + ".");
            }
        }
        FileConfiguration config = new YamlConfiguration();
        /* Single scavenger doesn't need to load */
        /*
         * try { config.load(playerData); } catch (FileNotFoundException e1) { } catch (IOException e1) { } catch (InvalidConfigurationException e1) {
         * }
         */
        List<String> mobList = new ArrayList<String>();
        for (Map.Entry<EntityType, Integer> entry : plugin.getMap(p.getName()).entrySet()) {
            if (plugin.currentMobs.containsKey(entry.getKey())) {
                mobList.add(entry.getKey() + " " + entry.getValue());
            }
        }
        config.set(scavengerID, mobList);
        try {
            config.save(playerData);
        } catch (IOException e) {
            plugin.getLogger().info("Could not save data file for player " + p.getDisplayName() + ".");
        }
    }

    protected static boolean loadPlayer(Player p) {
        File playerData = new File(plugin.getDataFolder(), "savedData" + File.pathSeparator + p.getDisplayName() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        try {
            config.load(playerData);
        } catch (FileNotFoundException e) {
            plugin.getLogger().info("Could not find savedData file for player " + p.getDisplayName() + ".");
            return false;
        } catch (IOException e) {
            plugin.getLogger().info("Could not load savedData file for player " + p.getDisplayName() + ".");
            return false;
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().info("Could not load savedData file for player " + p.getDisplayName() + ".");
            return false;
        }

        if (config.isList(scavengerID)) {
            Map<EntityType, Integer> map = plugin.getMap(p.getName());
            for (Object i : config.getList(scavengerID, new ArrayList<String>())) {
                try {
                    final String[] parts = i.toString().split(" ");
                    final int mobQuantity = Integer.parseInt(parts[1]);
                    final EntityType mobName = EntityType.fromName(parts[0]);
                    map.put(mobName, mobQuantity);
                } catch (Exception e) {
                }
            }
        }
        return true;
    }

}
