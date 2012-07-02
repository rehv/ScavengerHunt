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
	}

	public static void setDiscMap() {
		String formatter = "Music Disc - ";

		discMap.put(2256, formatter + "13");
		discMap.put(2257, formatter + "cat");
		discMap.put(2258, formatter + "blocks");
		discMap.put(2259, formatter + "chirp");
		discMap.put(2260, formatter + "far");
		discMap.put(2261, formatter + "mall");
		discMap.put(2262, formatter + "mellohi");
		discMap.put(2263, formatter + "stal");
		discMap.put(2264, formatter + "strad");
		discMap.put(2265, formatter + "ward");
		discMap.put(2266, formatter + "11");
	}

	public static String getPotionName(short potionDV) {
		return potionMap.get(potionDV).toString();
	}

	public static String getDiscName(int discDV) {
		return discMap.get(discDV).toString();
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
