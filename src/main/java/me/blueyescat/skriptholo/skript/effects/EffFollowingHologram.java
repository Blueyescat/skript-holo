package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.Listeners;
import me.blueyescat.skriptholo.util.Utils;

@Name("Following Hologram")
@Description("Makes a hologram start/stop following an entity.")
@Examples({"set {_directions::*} to 1.5 meters horizontally infront, 1 meter above and 0.5 meters right",
		"make the last created hologram follow player with offset {_directions::*}",
		"",
		"make all holograms stop following",
		"let hologram {variable} start following event-entity"})
@Since("1.0.0")
@RequiredPlugins("ProtocolLib")
public class EffFollowingHologram extends Effect {

	static {
		Skript.registerEffect(EffFollowingHologram.class,
				"(make|let) [holo[gram][s]] %holograms% (start following|follow) %entity% [[with] offset [(of|by|in|to)] [[the] direction] %-directions%]",
				"(make|let) [holo[gram][s]] %holograms% (stop following|unfollow)");
	}

	private Expression<Hologram> holograms;
	private Expression<Entity> entity;
	private Expression<Direction> offset;
	private boolean negative;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!Utils.hasPlugin("ProtocolLib")) {
			Skript.error("The following hologram feature requires ProtocolLib");
			return false;
		}
		holograms = (Expression<Hologram>) exprs[0];
		negative = matchedPattern == 1;
		if (!negative) {
			entity = (Expression<Entity>) exprs[1];
			offset = (Expression<Direction>) exprs[2];
		}
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
				Direction[] offset = null;
				if (this.offset != null) {
					offset = this.offset.getArray(e);
				}
				assert entity != null;
				Utils.makeHologramStartFollowing(holo, entity, offset);
			}
			Listeners.start();
		} else {
			for (Hologram holo : holograms.getArray(e)) {
				if (Utils.isFollowingHologram(holo))
					Utils.makeHologramStopFollowing(holo);
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (!negative) {
			return "make " + holograms.toString(e, debug) + " start following " + entity.toString(e, debug) +
					(offset != null ? " with offset " + offset.toString(e, debug) : "");
		} else {
			return "make " + holograms.toString(e, debug) + " stop following " +
					(entity != null ? entity.toString(e, debug) : "");
		}
	}

}
