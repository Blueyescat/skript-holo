package me.blueyescat.skriptholo.skript.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;

import me.blueyescat.skriptholo.util.HologramLinePickupEvent;

public class EvtHologramLineTouch extends SkriptEvent {

	static {
		Skript.registerEvent("Hologram Line Touch", EvtHologramLineTouch.class, HologramLinePickupEvent.class,
				"holo[gram] [line] touch")
				.description("Called while a player is trying to pickup an item in a hologram line. " +
						"See the `Make Hologram Line Touchable` effect.")
				.examples("on hologram touch:",
						"\tif hologram line is a stone:",
						"\t\tgive item of event-hologram-line to player")
				.since("1.0.0");

		EventValues.registerEventValue(HologramLinePickupEvent.class, Player.class, new Getter<Player, HologramLinePickupEvent>() {
			@Override
			public Player get(HologramLinePickupEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(HologramLinePickupEvent.class, Hologram.class, new Getter<Hologram, HologramLinePickupEvent>() {
			@Override
			public Hologram get(HologramLinePickupEvent e) {
				return e.getHologram();
			}
		}, 0);
		EventValues.registerEventValue(HologramLinePickupEvent.class, HologramLine.class, new Getter<HologramLine, HologramLinePickupEvent>() {
			@Override
			public HologramLine get(HologramLinePickupEvent e) {
				return e.getHologramLine();
			}
		}, 0);
		EventValues.registerEventValue(HologramLinePickupEvent.class, ItemType.class, new Getter<ItemType, HologramLinePickupEvent>() {
			@Override
			public ItemType get(HologramLinePickupEvent e) {
				return new ItemType(((ItemLine) e.getHologramLine()).getItemStack());
			}
		}, 0);
	}

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parser) {
		return true;
	}

	@Override
	public boolean check(Event e) {
		return e instanceof HologramLinePickupEvent;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "hologram line touch";
	}

}
