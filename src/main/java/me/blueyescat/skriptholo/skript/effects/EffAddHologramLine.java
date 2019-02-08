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

/**
 * @author Blueyescat
 */
@Name("Add Hologram Line")
@Description("Adds new lines to a hologram. " +
		"`insert` means adding after the specified line and moving the next lines below.")
@Examples({"prepend \"<light green>Test\" to the created hologram",
		"append a stone to {_holo}",
		"insert lines \"&cRedstone\" and a redstone in holo at line 2",
		"insert every diamond armor into the hologram at the 5th line"})
@Since("0.1.0")
public class EffAddHologramLine extends Effect {

	static {
		Skript.registerEffect(EffAddHologramLine.class,
				"(prepend|1¦append) [(10¦interact|20¦(touch|click)|30¦pickup)[-]able] [line[s]] %-strings/itemtypes% to [holo[gram][s]] %holograms%",
				"insert [(10¦interact|20¦(touch|click)|30¦pickup)[-]able] [line[s]] %-strings/itemtypes% in[to] [holo[gram][s]] %holograms% at line %number%",
				"insert [(10¦interact|20¦(touch|click)|30¦pickup)[-]able] %-strings/itemtypes% in[to] [holo[gram][s]] %holograms% at [the] %number%(st|nd|rd|th) line");
	}

	private Expression<?> lines;
	private Expression<Hologram> holograms;
	private Expression<Number> line;

	private static enum Modes {
		PREPEND, APPEND, INSERT
	}
	private Modes mode;

	private static enum InteractModes {
		ALL, TOUCHABLE, PICKUPABLE
	}
	private InteractModes interactMode;

	@Override
	@SuppressWarnings({"unchecked"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = exprs[0];
		holograms = (Expression<Hologram>) exprs[1];
		if (matchedPattern == 0) {
			mode = parseResult.mark == 0 ? Modes.PREPEND : Modes.APPEND;
		} else {
			mode = Modes.INSERT;
			line = (Expression<Number>) exprs[2];
		}
		if (parseResult.mark == 10 || parseResult.mark == 11)
			interactMode = InteractModes.ALL;
		else if (parseResult.mark == 20 || parseResult.mark == 21)
			interactMode = InteractModes.TOUCHABLE;
		else if (parseResult.mark == 30 || parseResult.mark == 31)
			interactMode = InteractModes.PICKUPABLE;
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
				if (interactMode == InteractModes.ALL) {
					Utils.addTouchHandler(addedLine);
					Utils.addPickupHandler(addedLine);
				} else if (interactMode == InteractModes.TOUCHABLE) {
					Utils.addTouchHandler(addedLine);
				} else if (interactMode == InteractModes.PICKUPABLE) {
					Utils.addPickupHandler(addedLine);
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
