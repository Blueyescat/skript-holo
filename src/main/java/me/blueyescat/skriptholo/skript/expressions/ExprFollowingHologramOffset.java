package me.blueyescat.skriptholo.skript.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

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

import me.blueyescat.skriptholo.SkriptHolo;

/**
 * @author Blueyescat
 */
@Name("Following Hologram Offset")
@Description({"Returns offset of a following hologram. Can be set."})
@Examples("delete offset of last holo" +
		"set following offset of {_hologram} to vector(0.5, 2.5, 0)")
@Since("0.1.0")
public class ExprFollowingHologramOffset extends SimpleExpression<Vector> {

	static {
		Skript.registerExpression(ExprFollowingHologramOffset.class, Vector.class, ExpressionType.SIMPLE,
				"[the] [following] offset[s] of [holo[gram][s]] %holograms%",
				"%holograms%'[s] [following] offset[s]");
	}

	private Expression<Hologram> holograms;

	@Override
	@SuppressWarnings({"unchecked"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		return true;
	}

	@Override
	protected Vector[] get(Event e) {
		List<Vector> offsets = new ArrayList<>();
		for (Hologram holo : holograms.getArray(e)) {
			for (int entityID : SkriptHolo.followingHolograms.keySet()) {
				Map<Hologram, Vector> holoMap = SkriptHolo.followingHolograms.get(entityID);
				for (Object o : holoMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					if (entry.getKey().equals(holo) && entry.getValue() != null)
						offsets.add((Vector) entry.getValue());
				}
			}
		}
		return offsets.toArray(new Vector[0]);
	}

	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET)
			return CollectionUtils.array(Vector.class);
		return null;
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		for (Hologram holo : holograms.getArray(e)) {
			for (int entityID : SkriptHolo.followingHolograms.keySet()) {
				Map<Hologram, Vector> holoMap = SkriptHolo.followingHolograms.get(entityID);
				for (Object o : holoMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					if (entry.getKey().equals(holo)) {
						if (mode == ChangeMode.SET)
							entry.setValue(delta[0]);
						else
							entry.setValue(null);
					}
				}
			}
		}
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Vector> getReturnType() {
		return Vector.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the offset of " + holograms.toString(e, debug);
	}

}
