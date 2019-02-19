package me.blueyescat.skriptholo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.CollectableLine;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;

import me.blueyescat.skriptholo.SkriptHolo;
import me.blueyescat.skriptholo.skript.effects.EffCreateHologram;

public class Utils {

	public static boolean hasPlugin(String name) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(name);
	}

	public static Plugin getPlugin(String name) {
		return Bukkit.getServer().getPluginManager().getPlugin(name);
	}

	public static List<HologramLine> getHologramLines(Hologram holo) {
		List<HologramLine> lines = new ArrayList<>();
		for (int l = 0; l < holo.size(); l++)
			lines.add(holo.getLine(l));
		return lines;
	}

	@SuppressWarnings("unchecked")
	public static void deleteHologram(Integer entityID, Hologram... holograms) {
		for (Hologram holo : holograms) {
			if (!holo.isDeleted())
				holo.delete();
			if (holo.equals(EffCreateHologram.lastCreated))
				EffCreateHologram.lastCreated = null;
			if (isFollowingHologram(holo)) {
				Iterator it;
				it = SkriptHolo.followingHologramsEntities.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					List<Hologram> holoList = (List<Hologram>) entry.getValue();
					holoList.removeIf(holo2 -> holo2.equals(holo));
					if (holoList.isEmpty())
						it.remove();
				}
				if (entityID == null) {
					it = SkriptHolo.followingHolograms.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						Map<Hologram, Vector> holoMap = (Map<Hologram, Vector>) entry.getValue();
						for (Object o2 : holoMap.entrySet()) {
							Map.Entry entry2 = (Map.Entry) o2;
							if (entry2.getKey().equals(holo)) {
								it.remove();
							}
						}
					}
				} else {
					SkriptHolo.followingHolograms.remove(entityID);
				}
			}
			SkriptHolo.followingHologramsList.remove(holo);
		}
	}

	public static void deleteHologram(Hologram... holograms) {
		deleteHologram(null, holograms);
	}

	public static void makeHologramStartFollowing(Hologram holo, Entity entity, Vector offset) {
		SkriptHolo.followingHologramsList.add(holo);

		Map<Hologram, Vector> holoMap;
		int entityID = entity.getEntityId();
		holoMap = SkriptHolo.followingHolograms.get(entityID);
		if (holoMap == null)
			holoMap = new HashMap<>();
		holoMap.put(holo, offset);
		SkriptHolo.followingHolograms.put(entityID, holoMap);

		List<Hologram> holoList;
		holoList = SkriptHolo.followingHologramsEntities.get(entity);
		if (holoList == null)
			holoList = new ArrayList<>();
		holoList.add(holo);
		SkriptHolo.followingHologramsEntities.put(entity, holoList);

		Location location = entity.getLocation().clone();
		if (offset != null)
			location.add(offset);
		if (holo.getWorld() == location.getWorld())
			holo.teleport(location);
	}

	@SuppressWarnings("unchecked")
	public static void makeHologramStopFollowing(Hologram holo) {
		Iterator it;
		it = SkriptHolo.followingHolograms.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Map<Hologram, Vector> holoMap = (Map<Hologram, Vector>) entry.getValue();
			Iterator it2 = holoMap.entrySet().iterator();
			while (it2.hasNext()) {
				Hologram holo2 = (Hologram) ((Map.Entry) it2.next()).getKey();
				if (holo2.equals(holo))
					it2.remove();
			}
			it.remove();
		}

		it = SkriptHolo.followingHologramsEntities.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			List<Hologram> holoList = (List<Hologram>) entry.getValue();
			holoList.removeIf(holo2 -> holo2.equals(holo));
			if (holoList.isEmpty())
				it.remove();
		}
		SkriptHolo.followingHologramsList.remove(holo);
	}

	public static boolean isFollowingHologram(Hologram holo) {
		return SkriptHolo.followingHologramsList.contains(holo);
	}

	public static void addTouchHandler(HologramLine line) {
		TouchableLine tl = (TouchableLine) line;
		if (tl.getTouchHandler() == null) {
			tl.setTouchHandler(player -> {
				HologramLineTouchEvent event = new HologramLineTouchEvent(player, tl);
				Bukkit.getPluginManager().callEvent(event);
			});
		}
	}

	public static void addPickupHandler(HologramLine line) {
		if (!(line instanceof ItemLine))
			return;
		CollectableLine tl = (CollectableLine) line;
		if (tl.getPickupHandler() == null) {
			tl.setPickupHandler(player -> {
				HologramLinePickupEvent event = new HologramLinePickupEvent(player, tl);
				Bukkit.getPluginManager().callEvent(event);
			});
		}
	}

}
