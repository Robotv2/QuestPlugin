package fr.robotv2.questplugin.storage.repository;

import fr.robotv2.questplugin.storage.Identifiable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Repository<ID, T extends Identifiable<ID>> {

    CompletableFuture<Optional<T>> select(ID id);

    CompletableFuture<List<T>> selectAll();

    CompletableFuture<Void> insert(T value);

    CompletableFuture<Void> update(ID id, T value);

    CompletableFuture<Void> upsert(T value);

    CompletableFuture<Void> remove(T value);

    CompletableFuture<Void> removeFromId(ID id);

    void init();

    void close();
}
