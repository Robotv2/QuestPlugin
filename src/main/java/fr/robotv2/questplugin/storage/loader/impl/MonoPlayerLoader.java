package fr.robotv2.questplugin.storage.loader.impl;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.DatabaseManager;
import fr.robotv2.questplugin.storage.loader.PlayerLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
        return manager.composePlayer(player, true)
                .thenCompose((questPlayer) -> manager.removeQuestsIfEnded(questPlayer).thenApply((ignored) -> questPlayer))
                .thenApply((questPlayer) -> {
                    final int amount = QuestPlugin.instance().getResetHandler().fillPlayer(questPlayer);
                    if(amount != 0) QuestPlugin.debug(amount + " quest(s) has been given to the player '" + player.getName() + "'.");
                    return questPlayer;
                })
                .thenRun(() -> {
                    QuestPlugin.debug("Data of player " + player.getName() + " have been loaded successfully.");
                })
                .exceptionally(throwable -> {
                    QuestPlugin.logger().log(Level.SEVERE, "An error occurred while loading data for player " + player.getName(), throwable);
                    return null;
                });
    }

    @Override
    public CompletableFuture<Void> unload(Player player) {
        return manager.savePlayer(player, true).thenRun(() -> {
            QuestPlugin.debug("Data of player " + player.getName() + " have been saved successfully.");
        }).exceptionally((throwable) -> {
            QuestPlugin.logger().log(Level.SEVERE, "An error occurred while saving data for player " + player.getName(), throwable);
            return null;
        });
    }
}
