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
		"disable placeholders for {_hologram}",
		"enable the placeholders in the holo",
		"disallow placeholders in {_holograms::*}"})
@Since("1.0.0")
public class EffAllowPlaceholdersInHologram extends Effect {

	static {
		Skript.registerEffect(EffAllowPlaceholdersInHologram.class,
				"(allow|enable) [the] placeholders (in|for) [holo[gram][s]] %holograms%",
				"(disallow|disable) [the] placeholders (in|for) [holo[gram][s]] %holograms%");
	}

	private Expression<Hologram> holograms;
	private boolean allow;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		allow = matchedPattern == 0;
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
