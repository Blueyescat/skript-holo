package me.blueyescat.skriptholo.skript.expressions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import me.blueyescat.skriptholo.skript.Types;
import me.blueyescat.skriptholo.skript.effects.EffCreateHologram;
import me.blueyescat.skriptholo.util.Util;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Comparator;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.Comparators;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

/**
 * @author Blueyescat
 */
@Name("Hologram Lines")
@Description({"Returns all lines of a hologram."})
@Examples({"loop lines of the last created hologram:",
		"\tif loop-hologram line is item line:",
		"\t\tdelete loop-hologram line"})
@Since("0.1.0")
public class ExprHologramLines extends SimpleExpression<HologramLine> {

	static {
		Skript.registerExpression(ExprHologramLines.class, HologramLine.class, ExpressionType.PROPERTY,
				"[all] [the] lines of [holo[gram][s]] %holograms%",
				"[holo[gram][s]] %holograms%'[s] [all] lines",
				"[the] line %number% of [holo[gram][s]] %holograms%",
				"[the] %number%(st|nd|rd|th) line[s] of [holo[gram][s]] %holograms%",
				"[the] (first|1¦last) line[s] of [holo[gram][s]] %holograms%");
	}

	private Expression<Hologram> holograms;
	private Expression<Number> line;
	private Kleenean firstLine = Kleenean.UNKNOWN;
	private boolean isSingle = true;

	@SuppressWarnings({"unchecked"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		// All lines
		Skript.info(String.valueOf(matchedPattern));
		if (matchedPattern <= 1) {
			holograms = (Expression<Hologram>) exprs[0];
			isSingle = false;
		// Line x
		} else if (matchedPattern <= 3) {
			line = (Expression<Number>) exprs[0];
			holograms = (Expression<Hologram>) exprs[1];
		// First/Last Line
		} else {
			firstLine = Kleenean.get(parseResult.mark == 0);
			holograms = (Expression<Hologram>) exprs[0];
		}
		return true;
	}

	@Override
	protected HologramLine[] get(Event e) {
		List<HologramLine> lines = new ArrayList<>();
		for (Hologram holo : holograms.getArray(e)) {
			if (this.line == null) {
				// All lines
				if (firstLine.isUnknown()) {
					lines.addAll(Util.getHologramLines(holo));
				// First/Last Line
				} else {
					int line = firstLine.isTrue() ? 0 : holo.size() - 1;
					lines.add(holo.getLine(line));
				}
			// Line x
			} else {
				Number l = this.line.getSingle(e);
				if (l == null)
					continue;
				int li = l.intValue() - 1;
				if (!(li >= 0 && li < holo.size()))
				continue;
				lines.add(holo.getLine(li));
			}
		}
		return lines.toArray(new HologramLine[0]);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		// All lines
		if (!isSingle) {
			switch (mode) {
				case ADD:
				case REMOVE:
				case REMOVE_ALL:
				case SET:
				case DELETE:
				case RESET:
					return CollectionUtils.array(String[].class, ItemType[].class);
			}
		} else {
			switch (mode) {
				case SET:
				case DELETE:
				case RESET:
					return CollectionUtils.array(String.class, ItemType.class);
			}
		}
		return null;
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		// All Lines
		if (!isSingle) {
			switch (mode) {
				case ADD:
					for (Hologram holo : holograms.getArray(e)) {
						for (Object o : delta) {
							if (o instanceof String)
								holo.appendTextLine((String) o);
							else
								holo.appendItemLine(((ItemType) o).getItem().getRandom());
						}
					}
					break;
				case REMOVE:
				case REMOVE_ALL:
					HologramLine removedLine;
					for (Hologram holo : holograms.getArray(e)) {
						for (int line = 0; line < holo.size(); line++) {
							removedLine = holo.getLine(line);
							for (Object o : delta) {
								if (o instanceof String) {
									if (removedLine instanceof TextLine) {
										if (Comparators.compare(((TextLine) removedLine).getText(), o).is(Comparator.Relation.EQUAL))
											removedLine.removeLine();
									}
								} else {
									if (removedLine instanceof ItemLine) {
										if (Comparators.compare(((ItemLine) removedLine).getItemStack(), o).is(Comparator.Relation.EQUAL))
											removedLine.removeLine();
									}
								}
							}
						}
					}
					break;
				case SET:
					for (Hologram holo : holograms.getArray(e)) {
						holo.clearLines();
						for (Object o : delta) {
							if (o instanceof String)
								holo.appendTextLine((String) o);
							else
								holo.appendItemLine(((ItemType) o).getItem().getRandom());
						}
					}
					break;
				case DELETE:
				case RESET:
					for (Hologram holo : holograms.getArray(e))
						holo.clearLines();
			}
		// Single lines will use changers of the HologramLine type
		} else {
			Types.hologramLineChanger.change(get(e), delta, mode);
		}
	}

	@Override
	public boolean isSingle() {
		return isSingle;
	}

	@Override
	public Class<? extends HologramLine> getReturnType() {
		return HologramLine.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (!isSingle)
			return "the lines of " + holograms.toString(e, debug);
		else {
			if (line == null)
				return "the " + (firstLine.isTrue() ? "first" : "last") + " line of " + holograms.toString(e, debug);
			return "the line " + line.toString(e, debug) + " of " + holograms.toString(e, debug);
		}
	}

}
