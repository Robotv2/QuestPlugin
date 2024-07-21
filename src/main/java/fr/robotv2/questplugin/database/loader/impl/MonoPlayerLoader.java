package fr.robotv2.questplugin.database.loader.impl;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.database.DatabaseManager;
import fr.robotv2.questplugin.database.loader.PlayerLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MonoPlayerLoader implements PlayerLoader, Listener {

    private final DatabaseManager manager;

    public MonoPlayerLoader(DatabaseManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        unload(event.getPlayer());
    }

    @Override
    public CompletableFuture<Void> load(Player player) {
        return manager.composePlayerAndCache(player).thenRun(() -> {
            QuestPlugin.debug("Data of player " + player.getName() + " have been loaded successfully.");
        }).exceptionally((throwable) -> {
            QuestPlugin.logger().log(Level.SEVERE, "An error occurred while loading data for player " + player.getName());
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> unload(Player player) {
        return manager.savePlayerAndRemoveFromCache(player).thenRun(() -> {
            QuestPlugin.debug("Data of player " + player.getName() + " have been saved successfully.");
        }).exceptionally((throwable) -> {
            QuestPlugin.logger().log(Level.SEVERE, "An error occurred while saving data for player " + player.getName());
            return null;
        });
    }
}
