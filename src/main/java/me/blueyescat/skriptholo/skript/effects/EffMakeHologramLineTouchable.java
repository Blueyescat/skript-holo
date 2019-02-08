package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;

import me.blueyescat.skriptholo.util.Utils;

/**
 * @author Blueyescat
 */
@Name("Make Hologram Line Touchable")
@Description("Makes a hologram line touchable or untouchable. " +
		"An **un**touchable line won't trigger the `Hologram Line Touch` event.")
@Examples({"make line 1 of last holo click-able",
		"make all lines of {_holo} untouchable"})
@Since("0.1.0")
public class EffMakeHologramLineTouchable extends Effect {

	static {
		Skript.registerEffect(EffMakeHologramLineTouchable.class,
				"make [holo[gram] line[s]] %hologramlines% [(1¦(un|non[-]))](touchable|click[-]able)");
	}

	private Expression<HologramLine> lines;
	private boolean touchable;

	@Override
	@SuppressWarnings({"unchecked"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = (Expression<HologramLine>) exprs[0];
		touchable = parseResult.mark == 0;
		return true;
	}

	@Override
	protected void execute(Event e) {
		if (touchable) {
			for (HologramLine line : lines.getArray(e)) {
				Utils.addTouchHandler(line);
			}
		} else {
			for (HologramLine line : lines.getArray(e)) {
				TouchableLine tl = (TouchableLine) line;
				if (tl.getTouchHandler() != null)
					tl.setTouchHandler(null);
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "make " + lines.toString() + (touchable ? " touchable" : " untouchable");
	}

}
