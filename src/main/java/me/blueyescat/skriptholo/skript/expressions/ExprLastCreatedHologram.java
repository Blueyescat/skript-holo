package me.blueyescat.skriptholo.skript.expressions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import me.blueyescat.skriptholo.skript.effects.EffCreateHologram;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;

/**
 * @author Blueyescat
 */
@Name("Last Created Hologram")
@Description({"Holds the hologram that was created most recently with the create hologram effect."})
@Examples({"set {_holo} to the created hologram"})
@Since("0.1.0")
public class ExprLastCreatedHologram extends SimpleExpression<Hologram> {

	static {
		Skript.registerExpression(ExprLastCreatedHologram.class, Hologram.class, ExpressionType.SIMPLE, "[the] [last[ly]] [(created|spawned)] holo[gram]");
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return true;
	}

	@Override
	protected Hologram[] get(Event e) {
		return CollectionUtils.array(EffCreateHologram.lastCreated);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.DELETE)
			return CollectionUtils.array();
		return null;
	}

	@SuppressWarnings("null")
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		EffCreateHologram.lastCreated.delete();
		EffCreateHologram.lastCreated = null;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Hologram> getReturnType() {
		return Hologram.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the last created hologram";
	}

}
