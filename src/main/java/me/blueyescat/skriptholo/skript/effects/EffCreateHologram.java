package me.blueyescat.skriptholo.skript.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.blueyescat.skriptholo.SkriptHolo;
import me.blueyescat.skriptholo.util.Utils;

/**
 * @author Blueyescat
 */
@Name("Create Hologram")
@Description("Creates a new hologram. The `Last Created Hologram` expression can be used to get the created hologram.")
@Examples({"create a new hologram with line \"test\"",
		"create holo with line \"test\" at the targeted entity",
		"create new hologram with line \"test\" that follows player for 10 seconds",
		"create holo with line \"test\" that follows the player with offset by vector(0, 2.4, 0) for 10 seconds",
		"create a hologram with lines \"&bDiamond Armor\" and every diamond armor"})
@Since("0.1.0")
public class EffCreateHologram extends Effect {

	static {
		Skript.registerEffect(EffCreateHologram.class,
				"(create|spawn) [a] [new] holo[gram] [with line[s] %-strings/itemtypes%] [%direction% %location%] [for %-timespan%]",
				"(create|spawn) [a] [new] holo[gram] [with line[s] %-strings/itemtypes%] that follows %entity% [with offset [(by|of)] %-vector%] [for %-timespan%]");
	}

	private Expression<?> lines;
	private Expression<Location> location;
	private Expression<Entity> entity;
	private Expression<Vector> offset;
	private Expression<Timespan> duration;
	private boolean isFollowing;

	@Nullable
	public static Hologram lastCreated = null;

	@Override
	@SuppressWarnings({"unchecked"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = exprs[0];
		if (matchedPattern == 0) {
			location = Direction.combine((Expression<Direction>) exprs[1], (Expression<Location>) exprs[2]);
			duration = (Expression<Timespan>) exprs[3];
		} else {
			isFollowing = true;
			if (!Utils.hasProtocolLib()) {
				Skript.error("The following hologram feature requires ProtocolLib");
				return false;
			}
			entity = (Expression<Entity>) exprs[1];
			offset = (Expression<Vector>) exprs[2];
			duration = (Expression<Timespan>) exprs[3];
		}
		return true;
	}

	@Override
	protected void execute(Event e) {
		Location location = null;
		Entity entity = null;
		Vector offset = null;
		if (!isFollowing) {
			if (this.location == null)
				return;
			location = this.location.getSingle(e);
		} else {
			if (this.entity != null) {
				entity = this.entity.getSingle(e);
				if (entity == null)
					return;
				location = entity.getLocation();
				if (this.offset != null) {
					offset = this.offset.getSingle(e);
					if (offset == null)
						return;
					location.add(offset);
				}
			}
		}

		Hologram holo = HologramsAPI.createHologram(SkriptHolo.getInstance(), location);
		lastCreated = holo;
		if (lines != null) {
			for (Object line : lines.getArray(e)) {
				if (line instanceof String) {
					holo.appendTextLine((String) line);
				} else if (line instanceof ItemType) {
					for (ItemStack item : ((ItemType) line).getItem().getAll())
						holo.appendItemLine(item);
				}
			}
		}
		if (isFollowing) {
			assert entity != null;
			Map<Hologram, Vector> holoMap;
			int entityID = entity.getEntityId();
			holoMap = SkriptHolo.followingHolograms.get(entityID);
			if (holoMap == null)
				holoMap = new HashMap<>();
			holoMap.put(holo, offset);
			SkriptHolo.followingHolograms.put(entityID, holoMap);

			List<Hologram> holoList;
			holoList = SkriptHolo.followingHologramsEntities.get(entity);
			if (holoList == null)
				holoList = new ArrayList<>();
			holoList.add(holo);
			SkriptHolo.followingHologramsEntities.put(entity, holoList);

			SkriptHolo.startFollowingHologramTasks();
		}
		if (duration != null) {
			Timespan duration = this.duration.getSingle(e);
			if (duration != null)
				Bukkit.getScheduler().runTaskLater(SkriptHolo.getInstance(), holo::delete, duration.getTicks_i());
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (!isFollowing)
			return "create hologram" + (lines != null ? (" with lines " + lines.toString(e, debug)) : "") + " "
					+ location.toString(e, debug) + (duration != null ? " for " + duration.toString(e, debug) : "");
		else
			return "create hologram" + (lines != null ? (" with lines " + lines.toString(e, debug)) : "")
					+ " that follows " + entity.toString(e, debug) + (duration != null ? " for "
					+ duration.toString(e, debug) : "");
	}

}
