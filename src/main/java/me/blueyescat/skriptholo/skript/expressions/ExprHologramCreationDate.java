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
import ch.njol.skript.util.Date;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

@Name("Hologram Creation Date")
@Description("Returns the creation date of a hologram.")
@Examples("if difference between creation date of event-hologram and now is greater than a hour:")
@Since("1.0.0")
public class ExprHologramCreationDate extends SimpleExpression<Date> {

	static {
		Skript.registerExpression(ExprHologramCreationDate.class, Date.class, ExpressionType.SIMPLE,
				"[the] (creation|spawned) date[s] of [holo[gram][s]] %holograms%",
				"%holograms%'[s] (creation|spawned) date[s]");
	}

	private Expression<Hologram> holograms;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		return true;
	}

	@Override
	protected Date[] get(Event e) {
		List<Date> dates = new ArrayList<>();
		for (Hologram holo : holograms.getArray(e))
			dates.add(new Date(holo.getCreationTimestamp()));
		return dates.toArray(new Date[0]);
	}

	@Override
	public boolean isSingle() {
		return holograms.isSingle();
	}

	@Override
	public Class<? extends Date> getReturnType() {
		return Date.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the creation date of " + holograms.toString(e, debug);
	}

}
