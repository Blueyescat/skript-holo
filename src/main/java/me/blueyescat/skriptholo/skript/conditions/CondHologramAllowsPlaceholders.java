package me.blueyescat.skriptholo.skript.conditions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

@Name("Hologram Allows Placeholders")
@Description("Checks whether the given hologram allows placeholders.")
@Examples({"if event-hologram allows placeholders:",
		"if hologram {_variable} disallows placeholders:",
		"if {_holograms::*} do not support placeholders:"})
@Since("1.0.0")
public class CondHologramAllowsPlaceholders extends Condition {

	static {
		Skript.registerCondition(CondHologramAllowsPlaceholders.class,
				"[holo[gram][s]] %holograms% (allow|support)[s] placeholders",
				"[holo[gram][s]] %holograms% disallow[s] placeholders",
				"[holo[gram][s]] %holograms% (doesn't|does not|don't|do not) (allow|support) placeholders");
	}

	private Expression<Hologram> holograms;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		setNegated(matchedPattern > 0);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return holograms.check(e, Hologram::isAllowPlaceholders, isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return holograms.toString(e, debug) + (isNegated() ? " dis" : " ") + "allows placeholders";
	}

}