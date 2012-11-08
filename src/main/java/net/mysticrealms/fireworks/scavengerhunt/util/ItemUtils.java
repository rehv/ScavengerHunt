package net.mysticrealms.fireworks.scavengerhunt.util;

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
}
