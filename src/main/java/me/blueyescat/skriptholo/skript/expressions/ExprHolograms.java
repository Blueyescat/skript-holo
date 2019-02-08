package me.blueyescat.skriptholo.skript.expressions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
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
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.blueyescat.skriptholo.SkriptHolo;
import me.blueyescat.skriptholo.util.Utils;

/**
 * @author Blueyescat
 */
@Name("All Holograms")
@Description("Returns all the holograms created by skript-holo or other plugins except HolographicDisplays.")
@Examples("loop all holograms:")
@Since("0.1.0")
public class ExprHolograms extends SimpleExpression<Hologram> {

	static {
		Skript.registerExpression(ExprHolograms.class, Hologram.class, ExpressionType.SIMPLE,
				"[all] [the] holo[gram]s [([created] by|of|under|from) skript-holo]",
				"[all] [the] holo[gram]s ([created] by|of|under|from) [the] [plugin[s]] %strings%");
	}

	private Expression<String> plugins;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 1)
			plugins = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	protected Hologram[] get(Event e) {
		if (plugins == null)
			return HologramsAPI.getHolograms(SkriptHolo.getInstance()).toArray(new Hologram[0]);
		List<Hologram> holograms = new ArrayList<>();
		for (String name : plugins.getArray(e)) {
			Plugin plugin = Utils.getPlugin(name);
			if (plugin != null)
				holograms.addAll(HologramsAPI.getHolograms(plugin));
		}
		return holograms.toArray(new Hologram[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Hologram> getReturnType() {
		return Hologram.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "all holograms created by " + (plugins != null ? plugins.toString(e, debug) : "skript-holo");
	}

}
