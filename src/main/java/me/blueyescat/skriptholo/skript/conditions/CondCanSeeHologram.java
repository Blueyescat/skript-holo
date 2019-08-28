package me.blueyescat.skriptholo.skript.conditions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.blueyescat.skriptholo.util.Utils;

@Name("Can See Hologram")
@Description("Checks whether the given players can see the given holograms. " +
		"Use the `Hologram Visibility` effect to hide/show holograms.")
@Examples({"if holo {_variable} is hidden from the player:",
		"if all players can see holograms {_list::*}:",
		"if hologram {_holo} is not visible for {_player}:",
		"if the player can see the holo {holograms::1}:"})
@Since("1.0.0")
@RequiredPlugins("ProtocolLib")
public class CondCanSeeHologram extends Condition {

	static {
		Skript.registerCondition(CondCanSeeHologram.class,
				"[holo[gram][s]] %holograms% (is|are) (visible|1¦(invisible|hidden)) (for|to|from) %players%",
				"%players% can see [holo[gram][s]] %holograms%",
				"[holo[gram][s]] %holograms% (is|are)(n't| not) (visible|1¦(invisible|hidden)) (for|to|from) %players%",
				"%players% can('t| not) see [holo[gram][s]] %holograms%");
	}

	private Expression<Player> players;
	private Expression<Hologram> holograms;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!Utils.hasPlugin("ProtocolLib")) {
			Skript.error("The hologram visibility feature requires ProtocolLib");
			return false;
		}
		if (matchedPattern == 1 || matchedPattern == 3) {
			players = (Expression<Player>) exprs[0];
			holograms = (Expression<Hologram>) exprs[1];
		} else {
			players = (Expression<Player>) exprs[1];
			holograms = (Expression<Hologram>) exprs[0];
		}
		setNegated(matchedPattern > 1 ^ parseResult.mark == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return players.check(e, player ->
				holograms.check(e, holo ->
						holo.getVisibilityManager().isVisibleTo(player)
				), isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return players.toString(e, debug) + " can " + (isNegated() ? "not " : "") + "see" + holograms.toString(e, debug);
	}

}
