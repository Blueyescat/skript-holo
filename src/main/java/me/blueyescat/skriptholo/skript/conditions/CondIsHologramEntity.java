package me.blueyescat.skriptholo.skript.conditions;

import org.bukkit.entity.Entity;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

@Name("Is Hologram Entity")
@Description("Checks whether the given entity is a part of a hologram. It is not possible to get the hologram.")
@Examples({"if targeted entity is a part of a hologram:",
		"if {_entities::*} are hologram entities:"})
@Since("1.0.0")
public class CondIsHologramEntity extends PropertyCondition<Entity> {

	static {
		register(CondIsHologramEntity.class, "[[a] part[s] of] [a] holo[gram] [entit(y|ies)]", "entities");
	}

	@Override
	public boolean check(Entity entity) {
		return HologramsAPI.isHologramEntity(entity);
	}

	@Override
	public String getPropertyName() {
		return "part of a hologram";
	}

}
