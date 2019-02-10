package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.SkriptHolo;
import me.blueyescat.skriptholo.util.Utils;

/**
 * @author Blueyescat
 */
@Name("Following Hologram")
@Description("Makes a hologram start/stop following an entity.")
@Examples({"make the last created hologram follow player",
		"make all holograms stop following",
		"let hologram {variable} start following event-entity"})
@Since("1.0.0")
public class EffFollowingHologram extends Effect {

	static {
		Skript.registerEffect(EffFollowingHologram.class,
				"(make|let) [holo[gram][s]] %holograms% (start following|follow) %entity% [with offset [(by|of)] %-vector%]",
				"(make|let) [holo[gram][s]] %holograms% (stop following|unfollow)");
	}

	private Expression<Hologram> holograms;
	private Expression<Entity> entity;
	private Expression<Vector> offset;
	private boolean negative;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		negative = matchedPattern == 1;
		if (!negative)
			entity = (Expression<Entity>) exprs[1];
			offset = (Expression<Vector>) exprs[2];
		return true;
	}

	@Override
	protected void execute(Event e) {
		if (!negative) {
			Entity entity = null;
			if (this.entity != null) {
				entity = this.entity.getSingle(e);
				if (entity == null)
					return;
			}
			for (Hologram holo : holograms.getArray(e)) {
				if (Utils.isFollowingHologram(holo))
					Utils.makeHologramStopFollowing(holo);
				Vector offset = null;
				if (this.offset != null)
					offset = this.offset.getSingle(e);
				Utils.makeHologramStartFollowing(holo, entity, offset);
			}
			SkriptHolo.startFollowingHologramTasks();
		} else {
			for (Hologram holo : holograms.getArray(e)) {
				if (Utils.isFollowingHologram(holo))
					System.out.println("test");
					Utils.makeHologramStopFollowing(holo);
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (!negative) {
			return "make " + holograms.toString(e, debug) + " start following " + entity.toString(e, debug) +
					(offset != null ? " with offset by " + offset.toString(e, debug) : "");
		} else {
			return "make " + holograms.toString(e, debug) + " stop following " +
					(entity != null ? entity.toString(e, debug) : "");
		}
	}

}
