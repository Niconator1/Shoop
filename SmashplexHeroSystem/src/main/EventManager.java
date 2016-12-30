package main;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import abilities.Cooldown;

public class EventManager implements Listener {
	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = new SmashPlayer(p);
		Smashplex.players.add(sp);
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != -1) {
				sp.removeHeroItems();
				for (int i = 0; i < Smashplex.cooldown.size(); i++) {
					Cooldown c = Smashplex.cooldown.get(i);
					if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
						Smashplex.cooldown.remove(i);
					}
				}
			}
			Smashplex.players.remove(sp);
		}
	}

	@EventHandler
	public void registerDoubleJump(PlayerToggleFlightEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != -1) {
				if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
					event.setCancelled(true);
					double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
					double x = Math.cos(yaw);
					double z = Math.sin(yaw);
					Vector v = new Vector(x, 0, z).normalize().multiply(0.77);
					p.setVelocity(new Vector(v.getX(), 0.77, v.getZ()));
				}
			}
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Player p = e.getPlayer();
				SmashPlayer sp = Smashplex.getSmashPlayer(p);
				if (sp != null) {
					if (sp.getSelectedHero() != -1) {
						if (Smashplex.isPrimaryReady(p)) {
							if (Smashplex.smash) {
								sp.doPrimary();
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void registerSlotSwitch(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != -1) {
				if (event.getNewSlot() == 1) {
					if (Smashplex.smash) {
						sp.doSecondary();
					}
				} else if (event.getNewSlot() == 2) {
					if (Smashplex.isSmashReady(p)) {
						if (Smashplex.smash) {
							sp.doSmash();
						}
					}
				}
				p.getInventory().setHeldItemSlot(0);
			}
		}
	}

	@EventHandler
	public void blockFoodEvent(FoodLevelChangeEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player p = (Player) entity;
			p.setFoodLevel(20);
			event.setCancelled(true);
		}
	}

}
