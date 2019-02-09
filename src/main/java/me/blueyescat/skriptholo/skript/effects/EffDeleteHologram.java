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

import com.gmail.filoghost.holographicdisplays.api.Hologram;

/**
 * @author Blueyescat
 */
@Name("Delete Hologram")
@Description({"Deletes a hologram. The hologram type has the delete changer already but this exists for variables " +
		"so `delete {_holo}` would delete the variable but you can do `delete hologram {_holo}` to avoid that.",
		"",
		"When you delete a hologram that is stored in a variable, the hologram object " +
		"will still exist in the variable but will not be usable. You should delete the variable too in this case."})
@Examples({"delete holo {_var}",
		"remove holograms {_holograms::*}"})
@Since("1.0.0")
public class EffDeleteHologram extends Effect {

	static {
		Skript.registerEffect(EffDeleteHologram.class,
				"(delete|remove|clear) holo[gram][s] %holograms%");
	}

	private Expression<Hologram> holograms;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		return true;
	}

	@Override
	protected void execute(Event e) {
		for (Hologram holo : holograms.getArray(e)) {
			holo.delete();
			if (holo.equals(EffCreateHologram.lastCreated))
				EffCreateHologram.lastCreated = null;
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "delete " + holograms.toString(e, debug);
	}

}
