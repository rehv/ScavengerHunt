/**
 * @author rehv@github.com - rf.rehv@gmail.com
 * 
 */

package net.mysticrealms.fireworks.scavengerhunt.util;

import java.util.HashMap;
import java.util.Map;

/* Debugging Imports
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.TreeMap;
 */

public class NameRetriever {
	public static Map<Short, String> potionMap = new HashMap<Short, String>();
	public static Map<Integer, String> discMap = new HashMap<Integer, String>();
	public static Map<Integer, String> enchantMap = new HashMap<Integer, String>();

	public static void setPotionMap() {
		String formatter = "Potion of ";

		// base potions
		potionMap.put((short) 0, "Water Bottle");
		potionMap.put((short) 16, "Awkward Potion");
		potionMap.put((short) 32, "Thick Potion");
		potionMap.put((short) 64, "Mundane Potion (Extended)");
		potionMap.put((short) 8192, "Mundane Potion");

		// unbrewable potions
		potionMap.put((short) 8289, formatter + "Regeneration II (Unbrewable)");
		potionMap.put((short) 8290, formatter + "Swiftness II (Unbrewable)");
		potionMap.put((short) 8297, formatter + "Strength II (Unbrewable)");
		potionMap.put((short) 8292, formatter + "Poison II (Unbrewable)");

		// effective potions. There are some skipped DV's, will try to cleanup later
		String potionType[] = new String[] { "Regeneration", "Swiftness", "Fire Resistance", "Poison", "Instant Health", "Weakness", "Strength", "Slowness", "Harming" };
		for (short i = 8193; i < 8193 + potionType.length - 4; i++) {
			potionMap.put(i, formatter + potionType[i - 8193]);
			potionMap.put((short) (i + 32), formatter + potionType[i - 8193] + " II");
			potionMap.put((short) (i + 64), formatter + potionType[i - 8193] + " (Extended)");
			potionMap.put((short) (i + 8192), "Splash " + formatter + potionType[i - 8193]);
			potionMap.put((short) (i + 8224), "Splash " + formatter + potionType[i - 8193] + " II");
			potionMap.put((short) (i + 8256), "Splash " + formatter + potionType[i - 8193] + " (Extended)");
		}

		// Potions 8198-8199 are skipped
		for (short i = 8200; i < 8200 + potionType.length - 6; i++) {
			potionMap.put(i, formatter + potionType[i - 8200 + 5]);
			potionMap.put((short) (i + 32), formatter + potionType[i - 8200 + 5] + " II");
			potionMap.put((short) (i + 64), formatter + potionType[i - 8200 + 5] + " (Extended)");
			potionMap.put((short) (i + 8192), "Splash " + formatter + potionType[i - 8200 + 5]);
			potionMap.put((short) (i + 8224), "Splash " + formatter + potionType[i - 8200 + 5] + " II");
			potionMap.put((short) (i + 8256), "Splash " + formatter + potionType[i - 8200 + 5] + " (Extended)");
		}

		// Harming potions skip 1 position
		short i = 8204;
		potionMap.put(i, formatter + potionType[i - 8200 + 4]);
		potionMap.put((short) (i + 32), formatter + potionType[i - 8200 + 4] + " II");
		potionMap.put((short) (i + 64), formatter + potionType[i - 8200 + 4] + " (Extended)");
		potionMap.put((short) (i + 8192), "Splash " + formatter + potionType[i - 8200 + 4]);
		potionMap.put((short) (i + 8224), "Splash " + formatter + potionType[i - 8200 + 4] + " II");
		potionMap.put((short) (i + 8256), "Splash " + formatter + potionType[i - 8200 + 4] + " (Extended)");

		// reverted potions
		potionMap.put((short) 8227, formatter + "Fire Resistance (Reverted)");
		potionMap.put((short) 16419, "Splash " + formatter + "Fire Resistance (Reverted)");
		potionMap.put((short) 8261, formatter + "Instant Health (Reverted)");
		potionMap.put((short) 16453, "Splash " + formatter + "Instant Health (Reverted)");
		potionMap.put((short) 8232, formatter + "Weakness (Reverted)");
		potionMap.put((short) 16424, "Splash " + formatter + "Weakness (Reverted)");
		potionMap.put((short) 8234, formatter + "Slowness (Reverted)");
		potionMap.put((short) 16426, "Splash " + formatter + "Slowness (Reverted)");
		potionMap.put((short) 8268, formatter + "Harming (Reverted)");
		potionMap.put((short) 16460, "Splash " + formatter + "Harming (Reverted)");

		// potions of Night Vision and Invisibility
		i = 8198;
		potionMap.put(i, formatter + "Night Vision");
		potionMap.put((short) (i + 64), formatter + "Night Vision" + " (Extended)");
		potionMap.put((short) (i + 8192), "Splash " + formatter + "Night Vision");
		potionMap.put((short) (i + 8256), "Splash " + formatter + "Night Vision" + " (Extended)");

		i = 8206;
		potionMap.put(i, formatter + "Invisibility");
		potionMap.put((short) (i + 64), formatter + "Invisibility" + " (Extended)");
		potionMap.put((short) (i + 8192), "Splash " + formatter + "Invisibility");
		potionMap.put((short) (i + 8256), "Splash " + formatter + "Invisibility" + " (Extended)");
	}

	public static void setDiscMap() {
		String formatter = "Music Disc - ";
		String[] discName = new String[] { "13", "cat", "blocks", "chirp", "far", "mall", "mellohi", "stal", "strad", "ward", "11" };
		for (int i = 0; i < discName.length; i++) {
			discMap.put(i + 2256, formatter + discName[i]);
		}
	}

	public static void setEnchantMap() {
		String[] armorEnchant = new String[] { "Protection", "Fire Protection", "Feather Falling", "Blast Protection", "Projectile Protection", "Respiration", "Aqua Affinity" };
		for (int i = 0; i < armorEnchant.length; i++) {
			enchantMap.put(i, armorEnchant[i]);
		}

		String[] weaponEnchant = new String[] { "Sharpness", "Smite", "Bane of Arthropods", "Knockback", "Fire Aspect", "Looting" };
		for (int i = 0; i < weaponEnchant.length; i++) {
			enchantMap.put(i + 16, weaponEnchant[i]);
		}

		String[] toolEnchant = new String[] { "Efficiency", "Silk Touch", "Unbreaking", "Fortune" };
		for (int i = 0; i < toolEnchant.length; i++) {
			enchantMap.put(i + 32, toolEnchant[i]);
		}

		String[] bowEnchant = new String[] { "Power", "Punch", "Flame", "Infinity" };
		for (int i = 0; i < bowEnchant.length; i++) {
			enchantMap.put(i + 48, bowEnchant[i]);
		}
	}

	public static String getPotionName(short potionDV) {
		return potionMap.get(potionDV).toString();
	}

	public static String getDiscName(int discDV) {
		return discMap.get(discDV).toString();
	}

	public static String getEnchantName(int enchID) {
		return enchantMap.get(enchID).toString();
	}

	// Prints potion map (debug)
	// public static void printPotionMap() {
	// List sortedKeys=new ArrayList(potionMap.keySet());
	// Collections.sort(sortedKeys);
	// Map<Short, String> treeMap = new TreeMap<Short, String>(potionMap);
	// for (short key : treeMap.keySet()) {
	// System.out.println(key+":"+potionMap.get(key));
	// }
	// }
}
