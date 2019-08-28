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

import me.blueyescat.skriptholo.util.Utils;

@Name("Make Hologram Line Click-able/Touchable")
@Description({"Makes a hologram line click-able or/and touchable. ",
		"",
		"- **Click-able:** when a player clicks on the line, the `Hologram Line Click` event will be triggered",
		"- **Touchable:** when a player tries to pickup the item in the line, the `Hologram Line Touch` event will be triggered",
		"- **Interactive:** includes both click-able and touchable"})
@Examples({"make 1st line of last holo click-able",
		"make all lines of {_holo} untouchable",
		"make the last line of {_hologram} non-interactive",
		"make line 2 of the created hologram interactive"})
@Since("1.0.0")
public class EffMakeHologramLineInteractive extends Effect {

	static {
		Skript.registerEffect(EffMakeHologramLineInteractive.class,
				"(make|let) [holo[gram] line[s]] %hologramlines% [(1¦(un|non[-]))]click[-]able",
				"(make|let) [holo[gram] line[s]] %hologramlines% [(1¦(un|non[-]))]touch[-]able",
				"(make|let) [holo[gram] line[s]] %hologramlines% [(1¦(non[-]|un))](interactive|interact[-]able)");
	}

	private Expression<HologramLine> lines;
	private boolean negated, clickable, touchable;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		negated = parseResult.mark == 1;
		lines = (Expression<HologramLine>) exprs[0];
		clickable = matchedPattern == 0 || matchedPattern == 2;
		touchable = matchedPattern == 1 || matchedPattern == 2;
		return true;
	}

	@Override
	protected void execute(Event e) {
		if (clickable) {
			if (!negated) {
				for (HologramLine line : lines.getArray(e))
					Utils.addTouchHandler(line);
			} else {
				for (HologramLine line : lines.getArray(e))
					Utils.addTouchHandler(line);
			}
		}
		if (touchable) {
			if (!negated) {
				for (HologramLine line : lines.getArray(e))
					Utils.addPickupHandler(line);
			} else {
				for (HologramLine line : lines.getArray(e))
					Utils.addPickupHandler(line);
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		String interact = "";
		if (clickable && touchable)
			interact = "interact-able ";
		else if (clickable)
			interact = "click-able ";
		else if (touchable)
			interact = "touchable ";
		return "make " + lines.toString(e, debug) + (negated ? " non-" : " " + interact);
	}

}
