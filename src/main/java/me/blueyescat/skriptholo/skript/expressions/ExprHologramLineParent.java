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
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

@Name("Parent Hologram of Hologram Line")
@Description("Returns the parent hologram of a hologram line.")
@Examples("set {_holo} to parent hologram of {_line}")
@Since("1.0.0")
public class ExprHologramLineParent extends SimpleExpression<Hologram> {

	static {
		Skript.registerExpression(ExprHologramLineParent.class, Hologram.class, ExpressionType.SIMPLE,
				"[the] [parent[s]] holo[gram][s] of [holo[gram] line[s]] %hologramlines%",
				"%hologramlines%'[s] [parent[s]] holo[gram][s]");
	}

	private Expression<HologramLine> lines;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = (Expression<HologramLine>) exprs[0];
		return true;
	}

	@Override
	protected Hologram[] get(Event e) {
		List<Hologram> holograms = new ArrayList<>();
		for (HologramLine line : lines.getArray(e))
			holograms.add(line.getParent());
		return holograms.toArray(new Hologram[0]);
	}

	@Override
	public boolean isSingle() {
		return lines.isSingle();
	}

	@Override
	public Class<? extends Hologram> getReturnType() {
		return Hologram.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the parent hologram of " + lines.toString(e, debug);
	}

}
