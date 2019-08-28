package me.blueyescat.skriptholo.skript.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.SkriptHolo;
import me.blueyescat.skriptholo.util.Utils;

@Name("Following Hologram Offset")
@Description("Returns offset (directions) of a following hologram. Can be set.")
@Examples({"delete offset of last holo",
		"set following offset of {_hologram} to 0.5 meters east and 2.5 meters above"})
@Since("1.0.0")
@RequiredPlugins("ProtocolLib")
public class ExprFollowingHologramOffset extends SimpleExpression<Direction> {

	static {
		Skript.registerExpression(ExprFollowingHologramOffset.class, Direction.class, ExpressionType.SIMPLE,
				"[the] [following] offset[s] of [holo[gram][s]] %holograms%",
				"%holograms%'[s] [following] offset[s]");
	}

	private Expression<Hologram> holograms;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!Utils.hasPlugin("ProtocolLib")) {
			Skript.error("The following hologram feature requires ProtocolLib");
			return false;
		}
		holograms = (Expression<Hologram>) exprs[0];
		return true;
	}

	@Override
	protected Direction[] get(Event e) {
		List<Direction> offsets = new ArrayList<>();
		for (Hologram holo : holograms.getArray(e)) {
			if (!Utils.isFollowingHologram(holo))
				continue;
			for (int entityID : SkriptHolo.followingHolograms.keySet()) {
				Map<Hologram, Direction[]> holoMap = SkriptHolo.followingHolograms.get(entityID);
				for (Object o : holoMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					if (entry.getKey().equals(holo) && entry.getValue() != null)
						offsets.addAll(Arrays.asList((Direction[]) entry.getValue()));
				}
			}
		}
		return offsets.toArray(new Direction[0]);
	}

	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET)
			return CollectionUtils.array(Direction[].class);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		for (Hologram holo : holograms.getArray(e)) {
			if (!Utils.isFollowingHologram(holo))
				continue;
			for (int entityID : SkriptHolo.followingHolograms.keySet()) {
				Map<Hologram, Direction[]> holoMap = SkriptHolo.followingHolograms.get(entityID);
				for (Object o : holoMap.entrySet()) {
					Map.Entry entry = (Map.Entry) o;
					if (entry.getKey().equals(holo)) {
						if (mode == ChangeMode.SET) {
							Direction[] directions = new Direction[delta.length];
							for (int i = 0; i < delta.length; i++)
								directions[i] = (Direction) delta[i];
							entry.setValue(directions);
						} else {
							entry.setValue(null);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Direction> getReturnType() {
		return Direction.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the offset of " + holograms.toString(e, debug);
	}

}
