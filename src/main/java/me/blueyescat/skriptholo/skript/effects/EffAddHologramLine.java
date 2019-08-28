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
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

import me.blueyescat.skriptholo.util.Utils;

@Name("Add Hologram Line")
@Description("Adds new lines to a hologram. " +
		"`insert` means adding after the specified line and moving the next lines below.")
@Examples({"append a stone to {_holo}",
		"prepend \"<light green>Test\" to the created hologram",
		"insert lines \"&cRedstone\" and a redstone in holo at line 2",
		"insert every diamond armor into the hologram at the 5th line"})
@Since("1.0.0")
public class EffAddHologramLine extends Effect {

	static {
		Skript.registerEffect(EffAddHologramLine.class,
				"append [((1¦click|2¦touch|3¦interact)[-]able|3¦interactive)] [line[s]] %-strings/itemtypes% to [holo[gram][s]] %holograms%",
				"prepend [((1¦click|2¦touch|3¦interact)[-]able|3¦interactive)] [line[s]] %-strings/itemtypes% to [holo[gram][s]] %holograms%",
				"insert [((1¦click|2¦touch|3¦interact)[-]able|3¦interactive)] [line[s]] %-strings/itemtypes% in[to] [holo[gram][s]] %holograms% at line %number%",
				"insert [((1¦click|2¦touch|3¦interact)[-]able|3¦interactive)] [line[s]] %-strings/itemtypes% in[to] [holo[gram][s]] %holograms% at [the] %number%(st|nd|rd|th) line");
	}

	private Modes mode;
	private boolean clickable, touchable;
	private Expression<?> lines;
	private Expression<Hologram> holograms;
	private Expression<Number> line;

	private enum Modes {
		APPEND, PREPEND, INSERT
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 0) {
			mode = Modes.APPEND;
		} else if (matchedPattern == 1) {
			mode = Modes.PREPEND;
		} else if (matchedPattern >= 2) {
			mode = Modes.INSERT;
			line = (Expression<Number>) exprs[2];
		}
		clickable = (parseResult.mark & 1) == 1;
		touchable = (parseResult.mark & 2) == 2;
		lines = exprs[0];
		holograms = (Expression<Hologram>) exprs[1];
		return true;
	}

	@Override
	protected void execute(Event e) {
		for (Hologram holo : holograms.getArray(e)) {
			if (holo.isDeleted())
				continue;
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
				HologramLine addedLine = null;
				if (mode == Modes.PREPEND || mode == Modes.INSERT) {
					if (mode == Modes.PREPEND)
						li = 0;
					if (line instanceof String) {
						addedLine = holo.insertTextLine(li++, (String) line);
					} else if (line instanceof ItemType) {
						for (ItemStack item : ((ItemType) line).getItem().getAll())
							addedLine = holo.insertItemLine(li++, item);
					}
				} else {
					if (line instanceof String) {
						addedLine = holo.appendTextLine((String) line);
					} else if (line instanceof ItemType) {
						for (ItemStack item : ((ItemType) line).getItem().getAll())
							addedLine = holo.appendItemLine(item);
					}
				}
				if (clickable)
					Utils.addTouchHandler(addedLine);
				if (touchable)
					Utils.addPickupHandler(addedLine);
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		String interact = null;
		if (clickable && touchable)
			interact = "interactive ";
		else if (clickable)
			interact = "click-able ";
		else if (touchable)
			interact = "touchable ";
		if (mode == Modes.INSERT)
			return "insert " + (interact != null ? interact + " lines" : "") + lines.toString(e, debug) + " into " + holograms.toString(e, debug) +
					" at line " + line.toString(e, debug);
		else
			return (mode == Modes.PREPEND ? "prepend " : "append ") + (interact != null ? interact + " lines" : "") +
					lines.toString(e, debug) + " to " + holograms.toString(e, debug) + " at line " + line.toString(e, debug);
	}

}
