package fr.robotv2.questplugin.storage.repository;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.DirtyAware;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.util.Futures;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCachedRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T> {

    private final ConcurrentMap<ID, T> cache = new ConcurrentHashMap<>();
    private boolean fullyLoaded = false;

    @Override
    public CompletableFuture<Optional<T>> select(ID id) {

        final T cachedValue = cache.get(id);

        if (cachedValue != null) {
            return CompletableFuture.completedFuture(Optional.of(cachedValue));
        }

        return selectFromDataSource(id).thenApply(optionalValue -> {
            optionalValue.ifPresent(value -> cache.put(id, value));
            return optionalValue;
        });
    }

    @Override
    public CompletableFuture<List<T>> selectAll() {

        if(fullyLoaded) {
            return CompletableFuture.completedFuture(new ArrayList<>(cache.values()));
        }

        return selectAllFromDataSource().thenApply(values -> {
            values.forEach(value -> cache.put(value.getId(), value));
            this.fullyLoaded = true;
            return values;
        });
    }

    @Override
    public CompletableFuture<Void> insert(T value) {
        return insertIntoDataSource(value).thenRun(() -> cache.put(value.getId(), value));
    }

    @Override
    public CompletableFuture<Void> update(ID id, T value) {
        return updateInDataSource(id, value).thenRun(() -> cache.put(id, value));
    }

    @Override
    public CompletableFuture<Void> upsert(T value) {
        return upsertInDataSource(value).thenRun(() -> cache.put(value.getId(), value));
    }

    @Override
    public CompletableFuture<Void> remove(T value) {
        return removeFromDataSource(value).thenRun(() -> cache.remove(value.getId()));
    }

    @Override
    public CompletableFuture<Void> removeFromId(ID id) {
        return removeFromIdInDataSource(id).thenRun(() -> cache.remove(id));
    }

    @Override
    public void init() {
        final String name = getGenericTypeClass().getSimpleName();
        QuestPlugin.logger().info("Loading " + name + "(s) into the plugin...");
        selectAll().thenAccept((list) -> QuestPlugin.logger().info("Successfully loaded " + list.size() + " " + name + "(s) into the plugin."));
    }

    @Override
    public void close() {

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        boolean isDirtyAware = DirtyAware.class.isAssignableFrom(getGenericTypeClass());

        if (isDirtyAware) {
            cache.values().stream().filter(value -> ((DirtyAware) value).isDirty()).forEach(value -> {
                futures.add(upsert(value));
                ((DirtyAware) value).setDirty(false);
            });
        } else {
            cache.values().forEach(value -> futures.add(upsert(value)));
        }

        Futures.ofAll(futures).join();
    }

    @SuppressWarnings("unchecked")
    private Class<T> getGenericTypeClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    // Abstract methods to be implemented by subclasses for actual data source operations
    protected abstract CompletableFuture<Optional<T>> selectFromDataSource(ID id);

    protected abstract CompletableFuture<List<T>> selectAllFromDataSource();

    protected abstract CompletableFuture<Void> insertIntoDataSource(T value);

    protected abstract CompletableFuture<Void> updateInDataSource(ID id, T value);

    protected abstract CompletableFuture<Void> upsertInDataSource(T value);

    protected abstract CompletableFuture<Void> removeFromDataSource(T value);

    protected abstract CompletableFuture<Void> removeFromIdInDataSource(ID id);
}
