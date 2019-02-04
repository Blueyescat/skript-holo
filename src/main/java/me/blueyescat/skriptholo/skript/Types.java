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

/**
 * @author Blueyescat
 */
public class Types {

	static {
		// Hologram Type
		Classes.registerClass(new ClassInfo<>(Hologram.class, "hologram")
				.user("holo(gram)?s?")
				.name("Hologram")
				.description("A HolographicDisplays hologram. Can be deleted using 'delete' or 'clear' changers." +
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

	}

}
