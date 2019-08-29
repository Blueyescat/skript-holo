package me.blueyescat.skriptholo;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Direction;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.filoghost.holographicdisplays.HolographicDisplays;
import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.util.Utils;

public class FollowingHologramListeners implements Listener {

	private final static boolean entityRemoveEventExists = Skript.classExists("com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent");

	public static void start() {
		if (SkriptHolo.startedFollowingHologramTasks)
			return;
		SkriptHolo.startedFollowingHologramTasks = true;

		Bukkit.getPluginManager().registerEvents(new FollowingHologramListeners(), SkriptHolo.getInstance());
		if (entityRemoveEventExists)
			Bukkit.getPluginManager().registerEvents(new FollowingHologramListeners.EntityRemoveListener(), SkriptHolo.getInstance());

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(
				new PacketAdapter(SkriptHolo.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE,
						PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						int entityID = event.getPacket().getIntegers().read(0);
						Map<Hologram, Direction[]> holoMap = SkriptHolo.followingHolograms.get(entityID);
						if (holoMap == null || holoMap.isEmpty())
							return;
						for (Object o : holoMap.entrySet()) {
							Map.Entry entry = (Map.Entry) o;
							Hologram holo = (Hologram) entry.getKey();
							if (holo.isDeleted()) {
								holoMap.remove(holo);
								continue;
							}
							Player player = event.getPlayer();
							Entity entity;
							try {
								entity = event.getPacket().getEntityModifier(event).read(0);
							} catch (Exception e) {
								// Use HolographicDisplays' workaround for the ProtocolLib bug
								entity = HolographicDisplays.getNMSManager()
										.getEntityFromID(player.getWorld(), event.getPacket().getIntegers().read(0));
							}
							if (player.equals(entity) && !holo.getVisibilityManager().isVisibleTo(player))
								continue;
							Location location = entity.getLocation().clone();
							if (holo.getWorld() == location.getWorld() && holo.getLocation().distance(location) != 0)
								holo.teleport(entry.getValue() != null ? Utils.offsetLocation(location, (Direction[]) entry.getValue()) : location);
						}
					}
				});

		if (!entityRemoveEventExists) {
			protocolManager.addPacketListener(
					new PacketAdapter(SkriptHolo.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_DESTROY) {
						@Override
						public void onPacketSending(PacketEvent event) {
							for (int entityID : event.getPacket().getIntegerArrays().read(0)) {
								if (SkriptHolo.followingHolograms.containsKey(entityID)) {
									Utils.cleanFollowingHolograms();
									return;
								}
							}
						}
					});

			new BukkitRunnable() {
				@Override
				public void run() {
					Utils.cleanFollowingHolograms();
				}
			}.runTaskTimerAsynchronously(SkriptHolo.getInstance(), 0, 20 * 5);

			Bukkit.getPluginManager().registerEvents(new FollowingHologramListeners.EntityDeathListener(), SkriptHolo.getInstance());
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		int entityID = event.getPlayer().getEntityId();
		Map<Hologram, Direction[]> holoMap = SkriptHolo.followingHolograms.get(entityID);
		if (holoMap == null || holoMap.isEmpty())
			return;
		for (Object o : holoMap.entrySet()) {
			Map.Entry entry = (Map.Entry) o;
			Hologram holo = (Hologram) entry.getKey();
			if (holo.isDeleted()) {
				holoMap.remove(holo);
				return;
			}
			Player player = event.getPlayer();
			if (!holo.getVisibilityManager().isVisibleTo(player))
				return;
			if (event.getTo() == null)
				return;
			Location location = event.getTo().clone();
			if (holo.getWorld() == location.getWorld() && holo.getLocation().distance(location) != 0)
				holo.teleport(entry.getValue() != null ? Utils.offsetLocation(location, (Direction[]) entry.getValue()) : location);
		}
	}

	static class EntityRemoveListener implements Listener {

		@EventHandler
		public void onEntityRemove(EntityRemoveFromWorldEvent event) {
			Utils.deleteFollowingHolograms(event.getEntity().getEntityId());
		}

	}

	static class EntityDeathListener implements Listener {

		@EventHandler
		public void onEntityDeath(EntityDeathEvent event) {
			Utils.deleteFollowingHolograms(event.getEntity().getEntityId());
		}

	}

}
