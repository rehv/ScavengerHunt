package net.mysticrealms.fireworks.scavengerhunt.util;

import java.util.Arrays;

import org.bukkit.entity.Player;

/**
 * Original implementation by desht
 * 
 * Credit to nisovin for removing the dependency on player.getTotalExperience()
 */
public class ExperienceUtils {
	public static final int MAX_LEVEL_SUPPORTED = 301;

	private static final int xpRequiredForNextLevel[] = new int[MAX_LEVEL_SUPPORTED];
	private static final int xpTotalToReachLevel[] = new int[MAX_LEVEL_SUPPORTED];

	static {
		xpTotalToReachLevel[0] = 0;
		for (int i = 0; i < xpTotalToReachLevel.length; i++) {
			if (i < 15) {
				xpRequiredForNextLevel[i] = 17;
				xpTotalToReachLevel[i+1] = 17 * (i+1); 
			}
			else if (i < 30) {
				xpRequiredForNextLevel[i] = (int) Math.ceil(3 * i - 28);
				xpTotalToReachLevel[i+1] = (int) Math.ceil(1.5 * (i+1) * (i+1) - 29.5 * (i+1) + 360);
			}
			else {
				xpRequiredForNextLevel[i] = (int) Math.ceil(7 * i - 148);
				xpTotalToReachLevel[i+1] = (int) Math.ceil(3.5 * (i+1) * (i+1) - 151.5 * (i+1) + 2220);
			}
		}
	}

	public static void changeExp(Player player, int amt) {
		int xp;
		int xpceil = xpTotalToReachLevel[MAX_LEVEL_SUPPORTED - 1] + xpRequiredForNextLevel[MAX_LEVEL_SUPPORTED - 1] - 1;

		if (player != null) {
			xp = getCurrentExp(player);
		} else {
			return;
		}
		xp = xp + amt;

		if (xp < 0) {
			xp = 0;
		}
		else if (xp > xpceil) {
			xp = xpceil;
		}

		int curLvl = player.getLevel();
		int newLvl = getCurrentLevel(xp);
		if (curLvl != newLvl) {
			player.setLevel(newLvl);
		}

		float pct = ((float) (xp - xpTotalToReachLevel[newLvl]) / (float) xpRequiredForNextLevel[newLvl]);
		player.setExp(pct);
	}

	public static int getCurrentExp(Player player) {
		int lvl = player.getLevel();
		return xpTotalToReachLevel[lvl] + (int) (xpRequiredForNextLevel[lvl] * player.getExp());
	}

	public static boolean hasExp(Player player, int amt) {
		return getCurrentExp(player) >= amt;
	}

	public static int getCurrentLevel(int exp) {
		if (exp <= 0) {
			return 0;
		}
		int pos = Arrays.binarySearch(xpTotalToReachLevel, exp);
		return pos < 0 ? -pos - 2 : pos;
	}
}