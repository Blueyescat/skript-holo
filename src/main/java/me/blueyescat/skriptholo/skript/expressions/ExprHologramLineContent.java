package me.blueyescat.skriptholo.skript.expressions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import me.blueyescat.skriptholo.skript.Types;

/**
 * @author Blueyescat
 */
@Name("Hologram Line Content")
@Description({"Returns content (text or item) of a hologram line. " +
		"Please note that there are hologram line to text and item converters so you can use a hologram line " +
		"like a text or an item (e.g. give first line of holo to player)"})
@Examples({"give items in lines of last created hologram to player",
		"message text of line 3 of {_holo}",
		"set {_contents::*} to contents of all lines of {_holo0"})
@Since("1.0.0")
@SuppressWarnings("unchecked")
public class ExprHologramLineContent<T> extends SimpleExpression<T> {

	static {
		Skript.registerExpression(ExprHologramLineContent.class, Object.class, ExpressionType.PROPERTY,
				"[the] (content|1¦(text|string)|2¦item)[s] (of|in) [holo[gram] line[s]] %hologramlines%",
				"%hologramlines%'[s] (content|1¦(text|string)|2¦item)[s]");
	}

	private final ExprHologramLineContent<?> source;
	private final Class<T> superType;

	private Expression<HologramLine> lines;
	private int type;

	public ExprHologramLineContent() {
		this(null, (Class<? extends T>) Object.class);
	}

	private ExprHologramLineContent(ExprHologramLineContent<?> source, Class<? extends T>... types) {
		this.source = source;
		if (source != null)
			lines = source.lines;
		superType = (Class<T>) Utils.getSuperType(types);
	}

	@Override
	public Expression<?> getSource() {
		return source == null ? this : source;
	}

	@Override
	public final <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
		return new ExprHologramLineContent<>(this, to);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		lines = (Expression<HologramLine>) exprs[0];
		type = parseResult.mark;
		return true;
	}

	@Override
	protected T[] get(Event e) {
		List<Object> contents = new ArrayList<>();
		for (HologramLine line : lines.getArray(e)) {
			if (line instanceof TextLine) {
				if (type == 0 || type == 1)
					contents.add(((TextLine) line).getText());
			} else if (line instanceof ItemLine) {
				if (type == 0 || type == 2)
					contents.add(new ItemType(((ItemLine) line).getItemStack()));
			}
		}
		try {
			return Converters.convertStrictly(contents.toArray(), superType);
		} catch (ClassCastException cce) {
			return (T[]) Array.newInstance(superType, 0);
		}
	}

	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		switch (mode) {
			case SET:
			case DELETE:
			case RESET:
				return CollectionUtils.array(String.class, ItemType.class);
		}
		return null;
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Types.hologramLineChanger.change(lines.getArray(e), delta, mode);
	}

	@Override
	public boolean isSingle() {
		return lines.isSingle();
	}

	@Override
	public Class<? extends T> getReturnType() {
		return superType;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		switch (type) {
			case 1:
				return "the text of " + lines.toString(e, debug);
			case 2:
				return "the item of " + lines.toString(e, debug);
			default:
				return "the content of " + lines.toString(e, debug);
		}
	}

}
