package me.blueyescat.skriptholo.skript.expressions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

// using 'hologram line', 'the holo line', 'holo-line', 'hologram-line' did not work
// correctly with the HologramLine type...

@NoDoc
public class ExprEventHologramLine extends EventValueExpression<HologramLine> {

	static {
		Skript.registerExpression(ExprEventHologramLine.class, HologramLine.class, ExpressionType.SIMPLE,
				"[the] holo[gram][( |-)]line[s]");
	}

	public ExprEventHologramLine() {
		super(HologramLine.class);
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "a hologram line";
	}

}
