package me.blueyescat.skriptholo.skript.conditions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.util.Utils;

@Name("Is Hologram Visible by Default")
@Description("Checks whether the given hologram is visible by default.")
@Examples({"if {_hologram} is visible by default:",
		"if holograms {_list::*} are hidden by default:"})
@Since("1.0.0")
@RequiredPlugins("ProtocolLib")
public class CondIsHologramVisibleByDefault extends Condition {

	static {
		Skript.registerCondition(CondIsHologramVisibleByDefault.class,
				"[holo[gram][s]] %holograms% (is|are) (visible|1¦(invisible|hidden)) by default",
				"[holo[gram][s]] %holograms% (isn't|is not|aren't|are not) (visible|1¦(invisible|hidden)) by default");
	}

	private Expression<Hologram> holograms;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!Utils.hasPlugin("ProtocolLib")) {
			Skript.error("The hologram visibility feature requires ProtocolLib");
			return false;
		}
		holograms = (Expression<Hologram>) exprs[0];
		setNegated(matchedPattern == 1 ^ parseResult.mark == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return holograms.check(e, holo -> holo.getVisibilityManager().isVisibleByDefault(), isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return holograms.toString(e, debug) + " is " + (isNegated() ? "not " : "") + "visible by default";
	}

}