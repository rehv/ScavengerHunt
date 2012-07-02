package net.mysticrealms.fireworks.scavengerhunt;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScavengerListener implements Listener {

	private ScavengerHunt plugin;

	public ScavengerListener(ScavengerHunt scavenger) {
		plugin = scavenger;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
		if (damageEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) damageEvent;
			Player p;
			if (entityEvent.getDamager() instanceof Player) {
				p = (Player) entityEvent.getDamager();
			} else if (entityEvent.getDamager() instanceof Projectile) {
				Projectile pr = (Projectile) entityEvent.getDamager();
				if (pr.getShooter() instanceof Player) {
					p = (Player) pr.getShooter();
				} else {
					return;
				}
			} else {
				return;
			}
			Map<EntityType, Integer> map = plugin.getMap(p.getName());
			map.put(event.getEntity().getType(), map.get(event.getEntity().getType()) + 1);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		long timeDiff = plugin.end - System.currentTimeMillis();
		String timeLeft = ScavengerMessager.timeFormatter((int)((timeDiff)/1000));
		if (plugin.isRunning) {
			p.sendMessage(ChatColor.DARK_RED + "There is a Scavenger Hunt happening!");
			p.sendMessage(ChatColor.DARK_RED + "You have: " + ChatColor.GOLD + timeLeft + "!");
			p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerItems" + ChatColor.DARK_RED + " to view objectives.");
			p.sendMessage(ChatColor.DARK_RED + "Use " + ChatColor.GOLD + "/scavengerRewards" + ChatColor.DARK_RED + " to view rewards.");
		}
	}
}
