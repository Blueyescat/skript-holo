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

@Name("Is Hologram Deleted")
@Description("Checks whether the given hologram is deleted. " +
		"When you delete a hologram that is stored in a variable, the hologram object will still " +
		"exist in the variable but will not be usable. This condition exists to check this case.")
@Examples("if hologram {variable} is deleted:")
@Since("1.0.0")
public class CondIsHologramDeleted extends Condition {

	static {
		Skript.registerCondition(CondIsHologramDeleted.class,
				"holo[gram][s] %holograms% (is|are) (deleted|removed|cleared)",
				"holo[gram][s] %holograms% (isn't|is not|aren't|are not) (deleted|removed|cleared)");
	}

	private Expression<Hologram> holograms;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return holograms.check(e, Hologram::isDeleted, isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return holograms.toString(e, debug) + " is " + (isNegated() ? "not " : "") + "deleted";
	}

}
