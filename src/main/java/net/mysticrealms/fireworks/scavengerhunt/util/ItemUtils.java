package net.mysticrealms.fireworks.scavengerhunt.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.mysticrealms.fireworks.scavengerhunt.ScavengerHunt;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
    public static ItemStack parseItem(String i) {
        boolean enchant = false;
        if (((String) i).contains(";")) {
            enchant = true;
        }
        final String[] parts = ((String) i).split(" ");
        final int[] intParts = new int[parts.length];
        for (int e = 0; e < parts.length; e++) {
            try {
                intParts[e] = Integer.parseInt(parts[e]);
            } catch (final NumberFormatException exception) {
                if (enchant) {
                    continue;
                } else {
                    return null;
                }
            }
        }
        ItemStack result = null;
        if (parts.length == 1 || (enchant && parts.length == 2)) {
            result = new ItemStack(intParts[0], 1);
        } else if (parts.length == 2 || (enchant && parts.length == 3)) {
            result = new ItemStack(intParts[0], intParts[1]);
        } else if (parts.length == 3) {
            result = new ItemStack(intParts[0], intParts[1], (short) intParts[2]);
        }
        // Might try to add this to a function later
        if (enchant) {
            String[] enchants = parts[parts.length - 1].split(";");
            for (String ench : enchants) {
                ench.trim();
                String[] enchparts = ench.split(":");
                if ((enchparts.length == 2 && (enchparts[0].matches("[0-9]*") && enchparts[1].matches("[0-9]*")))) {
                    int id = Integer.parseInt(enchparts[0]);
                    int lvl = Integer.parseInt(enchparts[1]);
                    Enchantment e = Enchantment.getById(id);
                    if (e != null) {
                        result.addUnsafeEnchantment(e, lvl);
                    }
                } else {
                    return null;
                }
            }
        }
        return result;
    }

    // debug
    public static void displayProbabilities(ScavengerHunt plugin, Map<ItemStack, Integer> rewards) {
        int totalWeight = weightTotals(rewards);
        final Charset encoding = StandardCharsets.UTF_8;
        List<String> lines = new ArrayList<String>();
        File debug = new File(plugin.getDataFolder(), "probabilities.txt");
        lines.add(String.format("%-20s %4s %4s %4s%n", "ITEM_TYPE", "ID", "DV", "%"));
        for (ItemStack i : rewards.keySet()) {
            lines.add(String.format("%-20s %4s %4s %3.3f%%%n", i.getType(), i.getTypeId(), i.getDurability(), ((float) rewards.get(i) / totalWeight) * 100));
        }
        Writer out;
        try {
            out = new OutputStreamWriter(new FileOutputStream(debug), encoding);
            for (String str : lines) {
                out.write(str);
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // it's here for possible future displays of the Logger class
    public static int weightTotals(Map<ItemStack, Integer> rewards) {
        int totalWeight = 0;

        for (int i : rewards.values()) {
            totalWeight += i;
        }

        return totalWeight;
    }

    public static String[] getProbability(String i) {
        String[] parts = ((String) i).split("=");

        if (parts.length != 2) {
            parts = new String[2];
            parts[0] = i;
            parts[1] = "100";
        }

        return parts;
    }

    public static List<ItemStack> setupProbabilities(Map<ItemStack, Integer> rewards) {
        List<ItemStack> rewardClone = new ArrayList<ItemStack>();

        for (ItemStack i : rewards.keySet()) {
            for (int j = 0; j < rewards.get(i); j++) {
                rewardClone.add(i);
            }
        }
        Collections.shuffle(rewardClone);

        return rewardClone;
    }
}
