package fr.robotv2.questplugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerStayOnlineEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public PlayerStayOnlineEvent(@NotNull final Player player) {
        super(player);
        this.player = player;
    }

    public int getStayTime() {
        return player.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
