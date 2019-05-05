package me.blueyescat.skriptholo;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.skript.effects.EffCreateHologram;

public class Listeners implements Listener {

	public static void start() {
		if (SkriptHolo.startedFollowingHologramTasks)
			return;
		SkriptHolo.startedFollowingHologramTasks = true;

		Bukkit.getPluginManager().registerEvents(new Listeners(), SkriptHolo.getInstance());

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(
				new PacketAdapter(SkriptHolo.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE,
						PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						int entityID = event.getPacket().getIntegers().read(0);
						Map<Hologram, Vector> holoMap = SkriptHolo.followingHolograms.get(entityID);
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
							Entity entity = event.getPacket().getEntityModifier(event).getValues().get(0);
							if (player.equals(entity) && !holo.getVisibilityManager().isVisibleTo(player))
								continue;
							Location location = entity.getLocation().clone();
							if (entry.getValue() != null)
								location.add((Vector) entry.getValue());
							if ((holo.getWorld() == location.getWorld()) || (holo.getLocation().distance(location) != 0))
								holo.teleport(location);
						}
					}
				});

		protocolManager.addPacketListener(
				new PacketAdapter(SkriptHolo.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_DESTROY) {
					@Override
					public void onPacketSending(PacketEvent event) {
						for (int entityID : event.getPacket().getIntegerArrays().read(0))
							SkriptHolo.deleteFollowingHolograms(entityID);
					}
				});

		new BukkitRunnable() {
			@Override
			@SuppressWarnings("unchecked")
			public void run() {
				for (Object o : SkriptHolo.followingHologramsEntities.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					Entity entity = (Entity) entry.getKey();
					if (!entity.isValid()) {
						for (Hologram holo : (List<Hologram>) entry.getValue()) {
							if (!holo.isDeleted())
								holo.delete();
							if (holo.equals(EffCreateHologram.lastCreated))
								EffCreateHologram.lastCreated = null;
							SkriptHolo.followingHologramsList.remove(holo);
						}
						SkriptHolo.followingHologramsEntities.remove(entity);
						SkriptHolo.followingHolograms.remove(entity.getEntityId());
					}
				}
			}
		}.runTaskTimerAsynchronously(SkriptHolo.getInstance(), 60, 0);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		int entityID = event.getPlayer().getEntityId();
		Map<Hologram, Vector> holoMap = SkriptHolo.followingHolograms.get(entityID);
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
			Location location = event.getTo().clone();
			if (entry.getValue() != null)
				location.add((Vector) entry.getValue());
			if ((holo.getWorld() == location.getWorld()) || (holo.getLocation().distance(location) != 0))
				holo.teleport(location);
		}
	}

}
