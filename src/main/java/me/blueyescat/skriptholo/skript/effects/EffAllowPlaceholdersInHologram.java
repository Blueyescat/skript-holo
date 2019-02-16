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

@Name("Allow Placeholders in Hologram")
@Description("Allows/disallows placeholders in a hologram. Disallowed by default.")
@Examples({"allow placeholders in the created hologram",
		"don't support placeholders in {_hologram}",
		"make the holo allow placeholders",
		"make {_holograms::*} don't support placeholders"})
@Since("1.0.0")
public class EffAllowPlaceholdersInHologram extends Effect {

	static {
		Skript.registerEffect(EffAllowPlaceholdersInHologram.class,
				"[(1¦dis)]allow placeholders in [holo[gram][s]] %holograms%",
				"[(1¦(don't|do not))] support placeholders in [holo[gram][s]] %holograms%",
				"(make|let) [holo[gram][s]] %holograms% [(1¦dis)]allow placeholders",
				"(make|let) [holo[gram][s]] %holograms% [(1¦(don't|do not))] support placeholders");
	}

	private Expression<Hologram> holograms;
	private boolean allow;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		allow = parseResult.mark == 0;
		return true;
	}

	@Override
	protected void execute(Event e) {
		for (Hologram holo : holograms.getArray(e))
			holo.setAllowPlaceholders(allow);
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return (!allow ? "dis" : "") + "allow placeholders in " + holograms.toString(e, debug);
	}

}
