package me.blueyescat.skriptholo;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.skript.effects.EffCreateHologram;
import me.blueyescat.skriptholo.util.Metrics;
import me.blueyescat.skriptholo.util.Utils;

public class SkriptHolo extends JavaPlugin implements Listener {

	private static SkriptHolo instance;
	private static SkriptAddon addonInstance;

	public static boolean startedFollowingHologramTasks = false;
	public static Map<Integer, Map<Hologram, Vector>> followingHolograms = new HashMap<>();
	public static Map<Entity, List<Hologram>> followingHologramsEntities = new ConcurrentHashMap<>();
	public static Set<Hologram> followingHologramsList = new HashSet<>();

	public SkriptHolo() {
		if (instance == null)
			instance = this;
		else
			throw new IllegalStateException();
	}

	@Override
	public void onEnable() {
		if (!Skript.isAcceptRegistrations()) {
			getServer().getPluginManager().disablePlugin(this);
			getLogger().severe("skript-holo can't be loaded when the server is already loaded! Plugin is disabled.");
			return;
		}

		try {
			SkriptAddon addonInstance = Skript.registerAddon(this); //.setLanguageFileDirectory("lang")
			addonInstance.loadClasses("me.blueyescat.skriptholo", "skript");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Metrics metrics = new Metrics(getInstance());
		metrics.addCustomChart(new Metrics.SimplePie("skript_version", () ->
				Skript.getInstance().getDescription().getVersion()));
		metrics.addCustomChart(new Metrics.SimplePie("holographicdisplays_version", () ->
				getServer().getPluginManager().getPlugin("HolographicDisplays").getDescription().getVersion()));
		getLogger().info("Started metrics!");
		getLogger().info("Finished loading!");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		int entityID = event.getPlayer().getEntityId();
		Map<Hologram, Vector> holoMap = followingHolograms.get(entityID);
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
			if ((holo.getWorld() == location.getWorld()) ||
					(holo.getLocation().distance(location) != 0))
				holo.teleport(location);
		}
	}

	private static void deleteFollowingHolograms(int entityID) {
		Map<Hologram, Vector> holoMap = followingHolograms.get(entityID);
		if (holoMap == null || holoMap.isEmpty())
			return;
		for (Object o : holoMap.entrySet()) {
			Map.Entry entry = (Map.Entry) o;
			Hologram holo = (Hologram) entry.getKey();
			Utils.deleteHologram(entityID, holo);
		}
	}

	public static void startFollowingHologramTasks() {
		if (startedFollowingHologramTasks)
			return;
		startedFollowingHologramTasks = true;

		Bukkit.getPluginManager().registerEvents(getInstance(), getInstance());

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(
				new PacketAdapter(getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE,
						PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_TELEPORT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						int entityID = event.getPacket().getIntegers().read(0);
						Map<Hologram, Vector> holoMap = followingHolograms.get(entityID);
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
							if ((holo.getWorld() == location.getWorld()) ||
									(holo.getLocation().distance(location) != 0))
								holo.teleport(location);
						}
					}
				});

		protocolManager.addPacketListener(
				new PacketAdapter(getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_DESTROY) {
					@Override
					public void onPacketSending(PacketEvent event) {
						for (int entityID : event.getPacket().getIntegerArrays().read(0))
							deleteFollowingHolograms(entityID);
					}
				});

		new BukkitRunnable() {
			@Override
			@SuppressWarnings("unchecked")
			public void run() {
				for (Object o : followingHologramsEntities.entrySet()) {
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
						followingHologramsEntities.remove(entity);
						followingHolograms.remove(entity.getEntityId());
					}
				}
			}
		}.runTaskTimerAsynchronously(getInstance(), 60, 0);
	}

	public static SkriptAddon getAddonInstance() {
		if (addonInstance == null)
			addonInstance = Skript.registerAddon(getInstance());
		return addonInstance;
	}

	public static SkriptHolo getInstance() {
		if (instance == null)
			throw new IllegalStateException();
		return instance;
	}

}
