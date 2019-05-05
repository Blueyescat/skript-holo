package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

@Name("Teleport Hologram")
@Description("Teleports a hologram to a specific location.")
@Examples({"teleport event-holo to the player",
		"teleport holograms {_holograms:*} to {_location}"})
@Since("1.0.0")
public class EffTeleportHologram extends Effect {

	static {
		Skript.registerEffect(EffTeleportHologram.class,
				"teleport [holo[gram][s]] %holograms% (to|%direction%) %location%");
	}

	private Expression<Hologram> holograms;
	private Expression<Location> location;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		location = Direction.combine((Expression<Direction>) exprs[1], (Expression<Location>) exprs[2]);
		return true;
	}

	@Override
	protected void execute(Event e) {
		Location location = this.location.getSingle(e);
		if (location == null)
			return;
		for (Hologram holo : holograms.getArray(e)) {
			if (!holo.isDeleted())
				holo.teleport(location);
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "teleport " + holograms.toString(e, debug) + " to " + location.toString(e, debug);
	}

}
