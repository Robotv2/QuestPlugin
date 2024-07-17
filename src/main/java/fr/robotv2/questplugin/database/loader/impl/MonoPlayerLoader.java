package fr.robotv2.questplugin.database.loader.impl;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.database.DatabaseManager;
import fr.robotv2.questplugin.database.loader.PlayerLoader;
import fr.robotv2.questplugin.event.QuestPlayerLoadEvent;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

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
        return manager.fetchAndCache(player)
                .thenAccept(QuestPlayer::checkEndedQuests)
                .whenComplete((ignored, throwable) -> {
                    Bukkit.getScheduler().runTask(QuestPlugin.instance(), () -> {
                        Bukkit.getPluginManager().callEvent(new QuestPlayerLoadEvent(player, throwable == null));
                    });
                });
    }

    @Override
    public CompletableFuture<Void> unload(Player player) {
        return manager.saveAndUnload(player);
    }
}
