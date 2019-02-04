package me.blueyescat.skriptholo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

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

import me.blueyescat.skriptholo.util.Metrics;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.gmail.filoghost.holographicdisplays.api.Hologram;

/**
 * @author Blueyescat
 */
public class SkriptHolo extends JavaPlugin implements Listener {

	private static SkriptHolo instance;
	private static SkriptAddon addonInstance;

	private static boolean startedFollowingHologramTasks = false;
	public static Map<Integer, Map<Hologram, Vector>> followingHolograms = new HashMap<>();
	public static Map<Entity, List<Hologram>> followingHologramsEntities = new ConcurrentHashMap<>();

	public SkriptHolo() {
		if (instance == null)
			instance = this;
		else
			throw new IllegalStateException();
	}

	@Override
	public void onEnable() {
		Logger logger = getLogger();

		if (!Skript.isAcceptRegistrations()) {
			getServer().getPluginManager().disablePlugin(this);
			logger.severe("skript-holo can't be loaded when the server is already loaded! Plugin is disabled.");
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
		logger.info("Started metrics!");
		logger.info("Finished loading!");
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
			Vector offset = (Vector) entry.getValue();
			Location to = event.getTo().clone();
			Location location = to.add(offset);
			if ((holo.getWorld() != to.getWorld()) ||
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
			holo.delete();
		}
		followingHolograms.remove(entityID);
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
							Vector offset = (Vector) entry.getValue();
							Location location = entity.getLocation().clone().add(offset);
							if ((holo.getWorld() != location.getWorld()) ||
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

			/*
			protocolManager.addPacketListener(
					new PacketAdapter(getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY,
							PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB, PacketType.Play.Server.SPAWN_ENTITY_LIVING,
							PacketType.Play.Server.SPAWN_ENTITY_PAINTING, PacketType.Play.Server.SPAWN_ENTITY_WEATHER,
							PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
						@Override
						public void onPacketSending(PacketEvent event) {
							Entity entity = event.getPacket().getEntityModifier(event).getValues().get(0);
							List<Entity> entityList = entitiesFollowedByHolograms.get(entityID);
							deleteFollowingHolograms(entity);
						}
					});
			*/

		new BukkitRunnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				for (Object o : followingHologramsEntities.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					Entity entity = (Entity) entry.getKey();
					if (!entity.isValid()) {
						for (Hologram holo : (List<Hologram>) entry.getValue())
							holo.delete();
						followingHologramsEntities.remove(entity);
						followingHolograms.remove(entity.getEntityId());
					}
				}
			}
		}.runTaskTimerAsynchronously(SkriptHolo.getInstance(), 60, 0);
	}

	public static boolean hasProtocolLib() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
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
