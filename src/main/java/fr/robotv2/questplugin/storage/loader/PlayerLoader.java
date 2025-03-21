package fr.robotv2.questplugin.storage.loader;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface PlayerLoader {

    CompletableFuture<Void> load(Player player);

    CompletableFuture<Void> unload(Player player);
}
