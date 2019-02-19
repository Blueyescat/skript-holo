package me.blueyescat.skriptholo.util;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;

public class HologramLineTouchEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private HologramLine line;

	public HologramLineTouchEvent(Player player, HologramLine line) {
		this.player = player;
		this.line = line;
	}

	public Player getPlayer() {
		return player;
	}

	public Hologram getHologram() {
		return line.getParent();
	}

	public HologramLine getHologramLine() {
		return line;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
