package me.blueyescat.skriptholo.skript;

import org.eclipse.jdt.annotation.Nullable;

import me.blueyescat.skriptholo.skript.effects.EffCreateHologram;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;

/**
 * @author Blueyescat
 */
public class Types {

	static {
		// Hologram
		Classes.registerClass(new ClassInfo<>(Hologram.class, "hologram")
				.user("holo(gram)?s?")
				.name("Hologram")
				.description("A HolographicDisplays hologram. Can be deleted using the 'delete/clear' changer." +
						"When you delete a hologram that is stored in a variable, the hologram object will still " +
						"exist in the variable but will not be usable. You should delete the variable in this case.")
				.since("0.1.0")
				.defaultExpression(new EventValueExpression<>(Hologram.class))
				.changer(new Changer<Hologram>() {
					@Override
					@Nullable
					public Class<?>[] acceptChange(ChangeMode mode) {
						if (mode == ChangeMode.DELETE)
							return CollectionUtils.array();
						return null;
					}

					@Override
					public void change(Hologram[] holograms, @Nullable Object[] delta, ChangeMode mode) {
						for (Hologram holo : holograms) {
							if (!holo.isDeleted()) {
								holo.delete();
								if (holo.equals(EffCreateHologram.lastCreated))
									EffCreateHologram.lastCreated = null;
							}
						}
					}
				})
				.parser(new Parser<Hologram>() {
					@Override
					public boolean canParse(ParseContext context) {
						return false;
					}

					@Override
					public String toString(Hologram holo, int flags) {
						return "hologram";
					}

					@SuppressWarnings("null")
					@Override
					public String toVariableNameString(Hologram holo) {
						return "hologram";
					}

					@Override
					public String getVariableNamePattern() {
						return "\\S+";
					}
				}));


		// Hologram Line
		Classes.registerClass(new ClassInfo<>(HologramLine.class, "hologramline")
				.user("holo(gram)?( |-)?lines?")
				.name("Hologram Line")
				.description("A line of a HolographicDisplays hologram. Can be deleted using the 'delete/clear' changer.")
				.since("0.1.0")
				.defaultExpression(new EventValueExpression<>(HologramLine.class))
				.changer(new Changer<HologramLine>() {
					@Override
					@Nullable
					public Class<?>[] acceptChange(ChangeMode mode) {
						if (mode == ChangeMode.DELETE)
							return CollectionUtils.array();
						return null;
					}

					@Override
					public void change(HologramLine[] holoLines, @Nullable Object[] delta, ChangeMode mode) {
						for (HologramLine holoLine : holoLines)
							holoLine.removeLine();
					}
				})
				.parser(new Parser<HologramLine>() {
					@Override
					public boolean canParse(ParseContext context) {
						return false;
					}

					@Override
					public String toString(HologramLine line, int flags) {
						if (line instanceof ItemLine)
							return "hologram item line";
						return "hologram line";
					}

					@SuppressWarnings("null")
					@Override
					public String toVariableNameString(HologramLine line) {
						if (line instanceof ItemLine)
							return "hologram item line";
						return "hologram line";
					}

					@Override
					public String getVariableNamePattern() {
						return "\\S+";
					}
				}));

	}

}
