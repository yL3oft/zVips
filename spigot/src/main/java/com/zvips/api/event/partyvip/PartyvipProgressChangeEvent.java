package com.zvips.api.event.partyvip;

import com.zvips.api.event.BalanceChangeType;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PartyvipProgressChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled;

    private int points;
    private BalanceChangeType type;

    public PartyvipProgressChangeEvent(int points, BalanceChangeType type) {
        this.points = points;
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public BalanceChangeType getType() {
        return this.type;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

}
