package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.entity.Player;
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
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.util.Utils;

@Name("Hologram Visibility")
@Description("Changes visibility of a hologram for the given players or makes a hologram invisible/visible by default." +
		"Resetting hologram visibility means setting the visibility to the default value (visible if not changed).")
@Examples({"hide the last created hologram from the player",
		"reveal holos {holograms::*} to {_players::*}",
		"reset the visibility of all holograms",
		"make the last holo invisible by default",
		"make all holograms visible by default"})
@Since("1.0.0")
@RequiredPlugins("ProtocolLib")
public class EffHologramVisibility extends Effect {

	static {
		Skript.registerEffect(EffHologramVisibility.class,
				"hide [holo[gram][s]] %holograms% (from|for) %players%",
				"reveal [holo[gram][s]] %holograms% (to|for|from) %players%",
				"reset [the] visibilit(y|ies) of [holo[gram][s]] %holograms% [for %-players%]",
				"(make|let) [holo[gram][s]] %holograms% (invisible|hidden) by default",
				"(make|let) [holo[gram][s]] %holograms% visible by default");
	}

	private Expression<Hologram> holograms;
	private Expression<Player> players;

	private enum Modes {
		HIDE, REVEAL, RESET, RESET_ALL, DEFAULT_INVISIBLE, DEFAULT_VISIBLE
	}
	private Modes mode;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!Utils.hasPlugin("ProtocolLib")) {
			Skript.error("The hologram visibility feature requires ProtocolLib");
			return false;
		}
		switch (matchedPattern) {
			case 0:
				mode = Modes.HIDE;
				break;
			case 1:
				mode = Modes.REVEAL;
				break;
			case 2:
				if (exprs[1] == null)
					mode = Modes.RESET_ALL;
				else
					mode = Modes.RESET;
				break;
			case 3:
				mode = Modes.DEFAULT_INVISIBLE;
				break;
			case 4:
				mode = Modes.DEFAULT_VISIBLE;
		}
		holograms = (Expression<Hologram>) exprs[0];
		if (matchedPattern < 3)
			players = (Expression<Player>) exprs[1];
		return true;
	}

	@Override
	protected void execute(Event e) {
		switch (mode) {
			case HIDE:
			case REVEAL:
				for (Player player : players.getArray(e)) {
					for (Hologram holo : holograms.getArray(e)) {
						if (mode == Modes.REVEAL)
							holo.getVisibilityManager().showTo(player);
						else
							holo.getVisibilityManager().hideTo(player);
					}
				}
				break;
			case RESET:
				for (Player player : players.getArray(e)) {
					for (Hologram holo : holograms.getArray(e))
						holo.getVisibilityManager().resetVisibility(player);
				}
				break;
			case RESET_ALL:
				for (Hologram holo : holograms.getArray(e))
					holo.getVisibilityManager().resetVisibilityAll();
				break;
			case DEFAULT_INVISIBLE:
			case DEFAULT_VISIBLE:
				for (Hologram holo : holograms.getArray(e))
					holo.getVisibilityManager().setVisibleByDefault(mode == Modes.DEFAULT_VISIBLE);
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		switch (mode) {
			case HIDE:
				return "hide " + holograms.toString(e, debug) + " from " + players.toString(e, debug);
			case REVEAL:
				return "show " + holograms.toString(e, debug) + " to " + players.toString(e, debug);
			case RESET:
			case RESET_ALL:
				return "reset visibility of " + holograms.toString(e, debug) +
						(players != null ? "for " + players.toString(e, debug) : "");
			case DEFAULT_INVISIBLE:
			case DEFAULT_VISIBLE:
				return "make " + holograms.toString(e, debug) +
						(mode == Modes.DEFAULT_INVISIBLE ? " in" : " ") + "visible by default";
			default:
				return null;
		}
	}

}
