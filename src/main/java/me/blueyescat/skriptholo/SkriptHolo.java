package me.blueyescat.skriptholo;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Direction;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.util.Metrics;
import me.blueyescat.skriptholo.util.Utils;

public class SkriptHolo extends JavaPlugin implements Listener {

	public static boolean startedFollowingHologramTasks = false;
	public static Map<Integer, Map<Hologram, Direction[]>> followingHolograms = new HashMap<>();
	public static Map<Entity, List<Hologram>> followingHologramsEntities = new ConcurrentHashMap<>();
	public static Set<Hologram> followingHologramsList = new HashSet<>();
	private static SkriptHolo instance;
	private static SkriptAddon addonInstance;

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

	static void deleteFollowingHolograms(int entityID) {
		Map<Hologram, Direction[]> holoMap = followingHolograms.get(entityID);
		if (holoMap == null || holoMap.isEmpty())
			return;
		for (Object o : holoMap.entrySet()) {
			Map.Entry entry = (Map.Entry) o;
			Hologram holo = (Hologram) entry.getKey();
			Utils.deleteHologram(entityID, holo);
		}
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
