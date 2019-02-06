package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

/**
 * @author Blueyescat
 */
@Name("Add Hologram Line")
@Description({"TODO"})
@Examples({"TODO"})
@Since("0.1.0")
public class EffAddHologramLine extends Effect {

	static {
		Skript.registerEffect(EffAddHologramLine.class,
				"(prepend|1¦append) [line[s]] %-strings/itemtypes% to [holo[gram][s]] %holograms%",
				"insert [line[s]] %-strings/itemtypes% in[to] [holo[gram][s]] %holograms% at line %number%",
				"insert [line[s]] %-strings/itemtypes% in[to] [holo[gram][s]] %holograms% at [the] %number%(st|nd|rd|th) line");
	}

	private Expression<?> lines;
	private Expression<Hologram> holograms;
	private Expression<Number> line;

	private static enum Modes {
		PREPEND, APPEND, INSERT
	}

	private Modes mode;

	@Override
	@SuppressWarnings({"unchecked"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = exprs[0];
		holograms = (Expression<Hologram>) exprs[1];
		if (matchedPattern == 1) {
			mode = Modes.INSERT;
			line = (Expression<Number>) exprs[2];
		} else {
			mode = parseResult.mark == 0 ? Modes.PREPEND : Modes.APPEND;
		}
		return true;
	}

	@Override
	protected void execute(Event e) {
		for (Hologram holo : holograms.getArray(e)) {
			int li = 0;
			if (mode == Modes.INSERT) {
				Number l = this.line.getSingle(e);
				if (l == null)
					return;
				li = l.intValue();
				if (!(li >= 0 && li <= holo.size()))
					continue;
			}
			for (Object line : lines.getArray(e)) {
				if (mode == Modes.PREPEND || mode == Modes.INSERT) {
					if (mode == Modes.PREPEND)
						li = 0;
					if (line instanceof String) {
						holo.insertTextLine(li, (String) line);
					} else if (line instanceof ItemType) {
						for (final ItemStack item : ((ItemType) line).getItem().getAll())
							holo.insertItemLine(li, item);
					}
					li++;
				} else {
					if (line instanceof String) {
						holo.appendTextLine((String) line);
					} else if (line instanceof ItemType) {
						for (final ItemStack item : ((ItemType) line).getItem().getAll())
							holo.appendItemLine(item);
					}
				}
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (mode == Modes.INSERT)
			return "insert " + lines.toString(e, debug) + " into " + holograms.toString(e, debug) +
					" at line " + line.toString(e, debug);
		else
			return (mode == Modes.PREPEND ? "prepend " : "append ") + lines.toString(e, debug) +
					" to " + holograms.toString(e, debug) + " at line " + line.toString(e, debug);
	}

}
