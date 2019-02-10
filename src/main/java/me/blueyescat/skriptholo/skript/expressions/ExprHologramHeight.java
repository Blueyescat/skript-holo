package me.blueyescat.skriptholo.skript.expressions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

/**
 * @author Blueyescat
 */
@Name("Hologram Height")
@Description("Returns the physical height of a hologram.")
@Examples("set {_height} to height of holo {_hologram}")
@Since("1.0.0")
public class ExprHologramHeight extends SimpleExpression<Number> {

	static {
		Skript.registerExpression(ExprHologramHeight.class, Number.class, ExpressionType.SIMPLE,
				"[the] [physical] height[s] of [holo[gram][s]] %holograms%",
				"%holograms%'[s] [physical] height[s]");
	}

	private Expression<Hologram> holograms;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		return true;
	}

	@Override
	protected Number[] get(Event e) {
		List<Number> heights = new ArrayList<>();
		for (Hologram holo : holograms.getArray(e)) {
			heights.add(holo.getHeight());
		}
		return heights.toArray(new Number[0]);
	}

	@Override
	public boolean isSingle() {
		return holograms.isSingle();
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the height of " + holograms.toString(e, debug);
	}

}
