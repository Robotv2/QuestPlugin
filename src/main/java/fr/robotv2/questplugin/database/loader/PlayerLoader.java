package fr.robotv2.questplugin.database.loader;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

public interface PlayerLoader {

    CompletableFuture<Void> load(Player player);

    CompletableFuture<Void> unload(Player player);
}
