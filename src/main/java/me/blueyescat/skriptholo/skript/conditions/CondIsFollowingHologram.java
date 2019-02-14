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

import me.blueyescat.skriptholo.util.Utils;

@Name("Is Following Hologram")
@Description("Checks whether the given hologram is a following hologram.")
@Examples("if {_holo} is a following hologram:")
@Since("1.0.0")
public class CondIsFollowingHologram extends Condition {

	static {
		Skript.registerCondition(CondIsFollowingHologram.class,
				"[holo[gram][s]] %holograms% (is|are) [a] following [holo[gram][s]]",
				"[holo[gram][s]] %holograms% (isn't|is not|aren't|are not) [a] following [holo[gram][s]]");
	}

	private Expression<Hologram> holograms;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return holograms.check(e, Utils::isFollowingHologram, isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return holograms.toString(e, debug) + " is " + (isNegated() ? "not " : "") + "following";
	}

}