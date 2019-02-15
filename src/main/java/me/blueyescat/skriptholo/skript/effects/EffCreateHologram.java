package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

import me.blueyescat.skriptholo.SkriptHolo;
import me.blueyescat.skriptholo.util.Utils;

@Name("Create Hologram")
@Description("Creates a new hologram. Besides the `store in %variable%`, " +
		"the `Last Created Hologram` expression can be used to get the created hologram.")
@Examples({"create a new hologram with line \"test\"",
		"create holo with line \"test\" at the targeted entity",
		"create new hologram with line \"test\" that follows player for 10 seconds",
		"create holo with line \"test\" that follows the player with offset by vector(0, 2.4, 0) for 10 seconds",
		"create a hologram with lines \"&bDiamond Armor\" and every diamond armor"})
@Since("1.0.0")
public class EffCreateHologram extends Effect {

	static {
		Skript.registerEffect(EffCreateHologram.class,
				"(create|spawn) [a] [new] holo[gram] [with [(1¦interact|2¦(touch|click)|3¦pickup)[-]able] line[s] %-strings/itemtypes%] [%direction% %location%] [for %-timespan%] [and store [it] in [[the] variable] %-objects%]",
				"(create|spawn) [a] [new] holo[gram] [with [(1¦interact|2¦(touch|click)|3¦pickup)[-]able] line[s] %-strings/itemtypes%] that follows %entity% [with offset [(by|of)] %-vector%] [for %-timespan%] [and store [it] in [[the] variable] %-objects%]");
	}

	private Expression<?> lines;
	private Expression<Location> location;
	private Expression<Entity> entity;
	private Expression<Vector> offset;
	private Expression<Timespan> duration;
	private boolean isFollowing;
	private Variable<?> variable;

	private enum InteractModes {
		ALL, TOUCHABLE, PICKUPABLE
	}
	private InteractModes interactMode;

	@Nullable
	public static Hologram lastCreated = null;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (parseResult.mark == 1)
			interactMode = InteractModes.ALL;
		else if (parseResult.mark == 2)
			interactMode = InteractModes.TOUCHABLE;
		else if (parseResult.mark == 3)
			interactMode = InteractModes.PICKUPABLE;
		lines = exprs[0];
		if (matchedPattern == 0) {
			location = Direction.combine((Expression<Direction>) exprs[1], (Expression<Location>) exprs[2]);
			duration = (Expression<Timespan>) exprs[3];
		} else {
			isFollowing = true;
			if (!Utils.hasPlugin("ProtocolLib")) {
				Skript.error("The following hologram feature requires ProtocolLib");
				return false;
			}
			entity = (Expression<Entity>) exprs[1];
			offset = (Expression<Vector>) exprs[2];
			duration = (Expression<Timespan>) exprs[3];
		}
		if (exprs[4] != null) {
			if (exprs[4] instanceof Variable) {
				variable = (Variable<?>) exprs[4];
			} else {
				Skript.error(exprs[4].toString() + " is not a variable");
				return false;
			}
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
				HologramLine addedLine;
				if (line instanceof String) {
					addedLine = holo.appendTextLine((String) line);
					if (interactMode == InteractModes.ALL || interactMode == InteractModes.TOUCHABLE) {
						Utils.addTouchHandler(addedLine);
					}
				} else if (line instanceof ItemType) {
					for (ItemStack item : ((ItemType) line).getItem().getAll()) {
						addedLine = holo.appendItemLine(item);
						if (interactMode == InteractModes.ALL) {
							Utils.addTouchHandler(addedLine);
							Utils.addPickupHandler(addedLine);
						} else if (interactMode == InteractModes.TOUCHABLE) {
							Utils.addTouchHandler(addedLine);
						} else if (interactMode == InteractModes.PICKUPABLE) {
							Utils.addPickupHandler(addedLine);
						}
					}
				}
			}
		}
		if (isFollowing) {
			assert entity != null;
			Utils.makeHologramStartFollowing(holo, entity, offset);
			SkriptHolo.startFollowingHologramTasks();
		}
		if (duration != null) {
			Timespan duration = this.duration.getSingle(e);
			if (duration != null)
				Bukkit.getScheduler().runTaskLater(SkriptHolo.getInstance(), holo::delete, duration.getTicks_i());
		}
		if (variable != null)
			variable.change(e, CollectionUtils.array(holo), (variable.isList() ? ChangeMode.ADD : ChangeMode.SET));
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (!isFollowing)
			return "create hologram" + (lines != null ? (" with lines " + lines.toString(e, debug)) : "") + " " +
					location.toString(e, debug) + (duration != null ? " for " + duration.toString(e, debug) : "") +
					(variable != null ? " and store it in " + variable.toString(e, debug) : "");
		else
			return "create hologram" + (lines != null ? (" with lines " + lines.toString(e, debug)) : "") +
					" that follows " + entity.toString(e, debug) + (offset != null ? " with offset by " +
					offset.toString(e, debug) : "") + (duration != null ? " for " + duration.toString(e, debug) : "") +
					(variable != null ? " and store it in " + variable.toString(e, debug) : "");
	}

}
