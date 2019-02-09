/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package me.blueyescat.skriptholo.skript.conditions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.gmail.filoghost.holographicdisplays.api.line.CollectableLine;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

@Name("Is Hologram Line Pickup-able")
@Description("Checks whether the given hologram line is pickup-able.")
@Examples("if line 3 of {_hologram} is pickup-able:")
@Since("1.0.0")
public class CondIsHologramLinePickupable extends Condition {

	static {
		Skript.registerCondition(CondIsHologramLinePickupable.class,
				"[holo[gram] line[s]] %hologramlines% (is|are) [(1¦(non[-]|un))]pick[( |-)]up[-]able",
				"[holo[gram] line[s]] %hologramlines% (isn't|is not|aren't|are not) [(1¦(non[-]|un))]pick[( |-)]up[-]able");
	}

	private Expression<HologramLine> lines;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = (Expression<HologramLine>) exprs[0];
		setNegated(matchedPattern == 1 ^ parseResult.mark == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return lines.check(e, line -> ((CollectableLine) line).getPickupHandler() != null, isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return lines.toString(e, debug) + " is " + (isNegated() ? "not " : "") + "pickup-able";
	}

}