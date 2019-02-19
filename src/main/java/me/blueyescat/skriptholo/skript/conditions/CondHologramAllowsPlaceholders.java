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
		"if placeholders in event-hologram are enabled:",
		"if hologram {_variable} disallows placeholders:",
		"if {_holo} doesn't allow placeholders:",
		"if placeholders for {_holograms::*} are disabled:"})
@Since("1.0.0")
public class CondHologramAllowsPlaceholders extends Condition {

	static {
		Skript.registerCondition(CondHologramAllowsPlaceholders.class,
				"[holo[gram][s]] %holograms% (allow|1¦disallow)[s] placeholders",
				"placeholders (in|for) [holo[gram][s]] %holograms% are (enabled|1¦disabled)",
				"[holo[gram][s]] %holograms% (doesn't|does not|don't|do not) (allow|1¦disallow) placeholders",
				"placeholders (in|for) [holo[gram][s]] %holograms% (aren't|are not) (enabled|1¦disabled)");
	}

	private Expression<Hologram> holograms;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		setNegated(matchedPattern > 1 ^ parseResult.mark == 1);
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