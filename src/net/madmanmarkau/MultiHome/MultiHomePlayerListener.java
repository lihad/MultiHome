package net.madmanmarkau.MultiHome;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MultiHomePlayerListener implements Listener {
	MultiHome plugin;
	
	public MultiHomePlayerListener(MultiHome plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (HomePermissions.has(player, "multihome.homeondeath") && Settings.isHomeOnDeathEnabled()) {
			Location location = plugin.homes.getHome(player, "");
			
			if (location != null) {
				event.setRespawnLocation(location);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		String player = event.getPlayer().getName();
		
		plugin.warmups.removeWarmup(player);
	}
}
