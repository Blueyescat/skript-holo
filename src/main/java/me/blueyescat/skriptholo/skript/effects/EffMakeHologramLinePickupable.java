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

import com.gmail.filoghost.holographicdisplays.api.line.CollectableLine;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

import me.blueyescat.skriptholo.util.Utils;

/**
 * @author Blueyescat
 */
@Name("Make Hologram Line Pickup-able")
@Description("Makes a hologram line pickup-able or non-pickup-able. " +
		"An **non-**pickup-able line won't trigger the `Hologram Line Pickup` event.")
@Examples({"make line 1 of hologram pickup-able",
		"make lines of {_holo} non-pickup-able"})
@Since("0.1.0")
public class EffMakeHologramLinePickupable extends Effect {

	static {
		Skript.registerEffect(EffMakeHologramLinePickupable.class,
				"make [holo[gram] line[s]] %hologramlines% [(1¦(non[-]|un))]pickup[-]able");
	}

	private Expression<HologramLine> lines;
	private boolean pickupable;

	@Override
	@SuppressWarnings({"unchecked"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = (Expression<HologramLine>) exprs[0];
		pickupable = parseResult.mark == 0;
		return true;
	}

	@Override
	protected void execute(Event e) {
		if (pickupable) {
			for (HologramLine line : lines.getArray(e)) {
				Utils.addPickupHandler(line);
			}
		} else {
			for (HologramLine line : lines.getArray(e)) {
				CollectableLine cl = (CollectableLine) line;
				if (cl.getPickupHandler() != null)
					cl.setPickupHandler(null);
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "make " + lines.toString() + (pickupable ? " pickup-able" : " non-pickup-able");
	}

}
