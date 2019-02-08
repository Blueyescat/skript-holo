package me.blueyescat.skriptholo.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

public class Utils {

	public static boolean hasPlugin(String name) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(name);
	}

	public static Plugin getPlugin(String name) {
		return Bukkit.getServer().getPluginManager().getPlugin(name);
	}

	public static List<HologramLine> getHologramLines(Hologram holo) {
		List<HologramLine> lines = new ArrayList<>();
		for (int line = 0; line < holo.size(); line++)
			lines.add(holo.getLine(line));
		return lines;
	}

}
