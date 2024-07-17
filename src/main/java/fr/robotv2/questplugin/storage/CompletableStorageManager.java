package fr.robotv2.questplugin.storage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CompletableStorageManager<ID, T extends Identifiable<ID>> {

    CompletableFuture<Optional<T>> select(ID id);

    CompletableFuture<List<T>> selectAll();

    CompletableFuture<Void> insert(T value);

    CompletableFuture<Void> update(ID id, T value);

    CompletableFuture<Void> upsert(T value);

    CompletableFuture<Void> remove(T value);

    CompletableFuture<Void> removeFromId(ID id);

    void close();

    static <ID, T extends Identifiable<ID>> CompletableStorageManager<ID, T> wrap(StorageManager<ID, T> storageManager) {
        return new CompletableStorageManager<ID, T>() {

            @Override
            public CompletableFuture<Optional<T>> select(ID id) {
                return CompletableFuture.completedFuture(storageManager.select(id));
            }

            @Override
            public CompletableFuture<List<T>> selectAll() {
                return CompletableFuture.completedFuture(storageManager.selectAll());
            }

            @Override
            public CompletableFuture<Void> insert(T value) {
                storageManager.insert(value);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> update(ID id, T value) {
                storageManager.update(id, value);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> upsert(T value) {
                storageManager.upsert(value);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> remove(T value) {
                storageManager.remove(value);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> removeFromId(ID id) {
                storageManager.removeFromId(id);
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void close() {
                storageManager.close();
            }
        };
    }
}
