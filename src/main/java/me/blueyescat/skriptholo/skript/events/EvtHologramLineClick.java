package me.blueyescat.skriptholo.skript.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

import me.blueyescat.skriptholo.util.HologramLineTouchEvent;

public class EvtHologramLineClick extends SkriptEvent {

	static {
		Skript.registerEvent("Hologram Line Click", EvtHologramLineClick.class, HologramLineTouchEvent.class,
				"holo[gram] [line] click")
				.description("Called when a player clicks on a hologram line. " +
						"See the `Make Hologram Line Click-able` effect.")
				.examples("on hologram click:",
						"\tif event-hologram-line is \"test\":")
				.since("1.0.0");

		EventValues.registerEventValue(HologramLineTouchEvent.class, Player.class, new Getter<Player, HologramLineTouchEvent>() {
			@Override
			public Player get(HologramLineTouchEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(HologramLineTouchEvent.class, Hologram.class, new Getter<Hologram, HologramLineTouchEvent>() {
			@Override
			public Hologram get(HologramLineTouchEvent e) {
				return e.getHologram();
			}
		}, 0);
		EventValues.registerEventValue(HologramLineTouchEvent.class, HologramLine.class, new Getter<HologramLine, HologramLineTouchEvent>() {
			@Override
			public HologramLine get(HologramLineTouchEvent e) {
				return e.getHologramLine();
			}
		}, 0);
	}

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parser) {
		return true;
	}

	@Override
	public boolean check(Event e) {
		return e instanceof HologramLineTouchEvent;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "hologram line click";
	}

}
