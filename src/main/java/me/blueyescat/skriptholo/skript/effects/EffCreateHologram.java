package me.blueyescat.skriptholo.skript.effects;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
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
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

import me.blueyescat.skriptholo.Listeners;
import me.blueyescat.skriptholo.SkriptHolo;
import me.blueyescat.skriptholo.util.Utils;

@Name("Create Hologram")
@Description({"Creates a new hologram.",
		"",
		"Besides the `store in %variable%`, the `Last Created Hologram` expression can be used to get the created hologram."})
@Examples({"create a new hologram with line \"test\" and store in {test}",
		"create new holo with lines \"line 1\", \"line 2\" and \"line 3\"",
		"create new hologram with line \"test\" that follows player offset by direction 2.3 meters above",
		"spawn a holo with click-able line \"Click\" that follows the spawned entity with offset (1.75 meters above and 1 meter infront)",
		"create a hologram with lines \"&bDiamond Armor\" and every diamond armor and store it in {holograms::1}",
		"spawn hologram with touchable line stone at {_location}"})
@Since("1.0.0")
public class EffCreateHologram extends Effect {

	@Nullable
	public static Hologram lastCreated = null;

	static {
		Skript.registerEffect(EffCreateHologram.class,
				"(create|spawn) [a] [new] holo[gram] [with [((1¦click|2¦touch|3¦interact)[-]able|3¦interactive)] line[s] %-strings/itemtypes%] " +
						"[%direction% %location%] [and store [it] in [[the] variable] %-objects%]",
				"(create|spawn) [a] [new] holo[gram] [with [((1¦click|2¦touch|3¦interact)[-]able|3¦interactive)] line[s] %-strings/itemtypes%] " +
						"that follows %entity% [[with] offset [(of|by|in|to)] [[the] direction] %-directions%] [and store [it] in [[the] variable] %-objects%]");
	}

	private Expression<?> lines;
	private Expression<Location> location;
	private Expression<Entity> entity;
	private Expression<Direction> offset;
	private Variable<?> variable;
	private boolean isFollowing, clickable, touchable;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		clickable = (parseResult.mark & 1) == 1;
		touchable = (parseResult.mark & 2) == 2;
		lines = exprs[0];
		if (matchedPattern == 0) {
			location = Direction.combine((Expression<Direction>) exprs[1], (Expression<Location>) exprs[2]);
		} else {
			if (!Utils.hasPlugin("ProtocolLib")) {
				Skript.error("The following hologram feature requires ProtocolLib");
				return false;
			}
			isFollowing = true;
			entity = (Expression<Entity>) exprs[1];
			offset = (Expression<Direction>) exprs[2];
		}
		if (exprs[3] != null) {
			if (exprs[3] instanceof Variable) {
				variable = (Variable<?>) exprs[3];
			} else {
				Skript.error(exprs[3].toString() + " is not a variable");
				return false;
			}
		}
		return true;
	}

	@Override
	protected void execute(Event e) {
		Location location = null;
		Entity entity = null;
		Direction[] offset = null;
		Object[] lines = null;
		if (this.lines != null) {
			lines = this.lines.getArray(e);
			if (lines.length == 0)
				return;
		}
		if (!isFollowing) {
			location = this.location.getSingle(e);
			if (location == null)
				return;
		} else if (this.entity != null) {
			entity = this.entity.getSingle(e);
			if (entity == null)
				return;
			if (this.offset != null) {
				offset = this.offset.getArray(e);
				if (offset.length == 0)
					return;
				location = Utils.offsetLocation(entity.getLocation(), offset);
			} else {
				location = entity.getLocation();
			}
		}
		Hologram holo = HologramsAPI.createHologram(SkriptHolo.getInstance(), location);
		lastCreated = holo;
		if (lines != null) {
			for (Object line : lines) {
				HologramLine addedLine;
				if (line instanceof String) {
					addedLine = holo.appendTextLine((String) line);
					if (clickable)
						Utils.addTouchHandler(addedLine);
				} else if (line instanceof ItemType) {
					for (ItemStack item : ((ItemType) line).getItem().getAll()) {
						addedLine = holo.appendItemLine(item);
						if (clickable)
							Utils.addTouchHandler(addedLine);
						if (touchable)
							Utils.addPickupHandler(addedLine);
					}
				}
			}
		}
		if (isFollowing) {
			assert entity != null;
			Utils.makeHologramStartFollowing(holo, entity, offset);
			Listeners.start();
		}
		if (variable != null)
			variable.change(e, CollectionUtils.array(holo), (variable.isList() ? ChangeMode.ADD : ChangeMode.SET));
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		String interact = "";
		if (clickable && touchable)
			interact = "interactive ";
		else if (clickable)
			interact = "click-able ";
		else if (touchable)
			interact = "touchable ";
		if (!isFollowing)
			return "create a hologram" + (lines != null ? (" with " + interact + "lines " + lines.toString(e, debug)) : "") +
					" " + location.toString(e, debug) + (variable != null ? " and store it in " +
					variable.toString(e, debug) : "");
		else
			return "create a hologram" + (lines != null ? (" with " + interact + "lines " + lines.toString(e, debug)) : "") +
					" that follows " + entity.toString(e, debug) + (offset != null ? " with offset " +
					offset.toString(e, debug) : "") + (variable != null ? " and store it in "
					+ variable.toString(e, debug) : "");
	}

}
