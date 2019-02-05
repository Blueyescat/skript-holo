package me.blueyescat.skriptholo.util;

import java.util.ArrayList;
import java.util.List;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

public class Utils {

	public static List<HologramLine> getHologramLines(Hologram holo) {
		List<HologramLine> lines = new ArrayList<>();
		for (int line = 0; line < holo.size(); line++)
			lines.add(holo.getLine(line));
		return lines;
	}

}
