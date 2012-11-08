package net.mysticrealms.fireworks.scavengerhunt;

import java.util.Map;

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
            ScavengerSaver.savePlayer(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (plugin.isRunning) {
            if (!plugin.playerList.contains(p.getDisplayName())) {
                plugin.messager.displayJoinCurrent(p);
            } else if (!plugin.currentMobs.isEmpty()) {
                if (ScavengerSaver.loadPlayer(p)) {
                    plugin.messager.displayRejoin(p);
                } else {
                    plugin.messager.displayRejoinFailed(p);
                }
            }
        }
    }
}
