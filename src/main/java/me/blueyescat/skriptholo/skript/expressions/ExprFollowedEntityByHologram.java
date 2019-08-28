package me.blueyescat.skriptholo.skript.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
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

import me.blueyescat.skriptholo.SkriptHolo;

@Name("Followed Entity of Hologram")
@Description("Returns the followed entity of a hologram. " +
		"Use the `Following Hologram` effect to make an exiting hologram start/stop following an entity.")
@Examples({"kill the followed entity of last hologram",
		"set {_entities::*} to the entities followed by holograms {_list::*}"})
@Since("1.0.0")
public class ExprFollowedEntityByHologram extends SimpleExpression<Entity> {

	static {
		Skript.registerExpression(ExprFollowedEntityByHologram.class, Entity.class, ExpressionType.SIMPLE,
				"[the] followed entit(y|ies) (of|by) [holo[gram][s]] %holograms%",
				"[the] entit(y|ies) followed by [holo[gram][s]] %holograms%");
	}

	private Expression<Hologram> holograms;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		holograms = (Expression<Hologram>) exprs[0];
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Entity[] get(Event e) {
		List<Entity> entities = new ArrayList<>();
		for (Hologram holo : holograms.getArray(e)) {
			for (Object o : SkriptHolo.followingHologramsEntities.entrySet()) {
				Map.Entry entry = (Map.Entry) o;
				for (Hologram holo2 : ((List<Hologram>) entry.getValue())) {
					if (holo2.equals(holo))
						entities.add((Entity) entry.getKey());
				}
			}
		}
		return entities.toArray(new Entity[0]);
	}

	@Override
	public boolean isSingle() {
		return holograms.isSingle();
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the followed entity by " + holograms.toString(e, debug);
	}

}
