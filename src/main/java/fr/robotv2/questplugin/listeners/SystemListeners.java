package fr.robotv2.questplugin.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.robotv2.questplugin.event.PlayerMoveFullBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SystemListeners implements Listener {

    public static final Cache<UUID, Boolean> PLAYER_MOVE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(200, TimeUnit.MILLISECONDS)
            .build();

    // event handler for : ActualPlayerMoveEvent
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if(to == null) {
            return;
        }

        final double xFrom = from.getX();
        final double yFrom = from.getY();
        final double zFrom = from.getZ();

        final double xTo = to.getX();
        final double yTo = to.getY();
        final double zTo = to.getZ();

        if(xFrom == xTo && yFrom == yTo && zFrom == zTo) {
            // player only move their head.
            return;
        }

        final UUID uuid = event.getPlayer().getUniqueId();
        if(PLAYER_MOVE_CACHE.getIfPresent(uuid) != null) {
            return;
        }

        PLAYER_MOVE_CACHE.put(uuid, Boolean.TRUE);

        final PlayerMoveFullBlockEvent actualPlayerMoveEvent = new PlayerMoveFullBlockEvent(event.getPlayer(), event.getFrom(), event.getTo());
        Bukkit.getPluginManager().callEvent(actualPlayerMoveEvent);

        if(actualPlayerMoveEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
