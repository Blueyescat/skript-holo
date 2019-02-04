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

/**
 * @author Blueyescat
 */
@Name("Hologram Lines")
@Description({"Returns all lines of a hologram."})
@Examples({"loop lines of the last created hologram:",
		"\tif loop-hologram line is item line:",
		"\t\tdelete loop-hologram line"})
@Since("0.1.0")
public class ExprHologramLines extends SimpleExpression<HologramLine> {

	static {
		Skript.registerExpression(ExprHologramLines.class, HologramLine.class, ExpressionType.PROPERTY,
				"[all] [the] lines of [holo[gram][s]] %holograms%",
				"[holo[gram][s]] %holograms%'[s] [all] lines",
				"[the] line %number% of [holo[gram][s]] %holograms%",
				"[the] %number%(st|nd|rd|th) line[s] of [holo[gram][s]] %holograms%");
	}

	private Expression<Hologram> holograms;
	private Expression<Number> line;

	@SuppressWarnings({"unchecked"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern <= 1) {
			holograms = (Expression<Hologram>) exprs[0];
		} else {
			line = (Expression<Number>) exprs[0];
			holograms = (Expression<Hologram>) exprs[1];
		}
		return true;
	}

	@Override
	protected HologramLine[] get(Event e) {
		List<HologramLine> lines = new ArrayList<>();
		for (Hologram holo : holograms.getArray(e)) {
			if (this.line == null) {
				for (int line = 0; line < holo.size(); line++)
					lines.add(holo.getLine(line));
			} else {
				Number l = this.line.getSingle(e);
				if (l == null)
					continue;
				int li = l.intValue() - 1;
				if (!(li >= 0 && li < holo.size()))
				continue;
				lines.add(holo.getLine(li));
			}
		}
		return lines.toArray(new HologramLine[0]);
	}

	@Override
	public boolean isSingle() {
		return line != null;
	}

	@Override
	public Class<? extends HologramLine> getReturnType() {
		return HologramLine.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (line == null)
			return "the lines of " + holograms.toString(e, debug);
		else
			return "the line " + line.toString(e, debug) + " of " + holograms.toString(e, debug);
	}

}
