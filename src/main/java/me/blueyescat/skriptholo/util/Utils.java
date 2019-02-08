package me.blueyescat.skriptholo.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.CollectableLine;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;

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
				HologramLineTouchEvent event = new HologramLineTouchEvent(player, tl);
				Bukkit.getPluginManager().callEvent(event);
			});
		}
	}

}
