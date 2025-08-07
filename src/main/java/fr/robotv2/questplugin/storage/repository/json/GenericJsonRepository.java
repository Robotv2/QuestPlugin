package fr.robotv2.questplugin.storage.repository.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.repository.AbstractCachedRepository;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class GenericJsonRepository<ID, T extends Identifiable<ID>> extends AbstractCachedRepository<ID, T> {

    public static final Gson GSON;

    static {
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC);
        builder.setPrettyPrinting();
        builder.serializeNulls();
        builder.enableComplexMapKeySerialization();
        GSON = builder.create();
    }

    private final Map<ID, File> fileCache = new ConcurrentHashMap<>();

    private final File folder;
    private final Class<T> tClass;

    public GenericJsonRepository(File folder, Class<T> tClass) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.folder = folder;
        this.tClass = tClass;
    }

    @Override
    public CompletableFuture<Optional<T>> selectFromDataSource(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.ofNullable(fromFile(getFileFor(id), tClass));
            } catch (FileNotFoundException fileNotFoundException) {
                return Optional.empty();
            } catch (IOException exception) {
                throw new RuntimeException("Failed to load data with id " + id, exception);
            }
        });
    }

    @Override
    public CompletableFuture<List<T>> selectAllFromDataSource() {
        return CompletableFuture.supplyAsync(() -> {
            final List<T> allValues = new ArrayList<>();
            final File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null) {
                return allValues;
            }

            for (File file : files) {
                try {
                    final T value = fromFile(file, tClass);
                    if (value != null) {
                        allValues.add(value);
                        fileCache.put(value.getUID(), file);
                    }
                } catch (IOException exception) {
                    throw new RuntimeException("Failed to load data from file " + file.getAbsolutePath(), exception);
                }
            }

            return allValues;
        });
    }

    @Override
    public CompletableFuture<Void> insertIntoDataSource(T value) {
        return CompletableFuture.runAsync(() -> {
            try {
                writeToFile(getFileFor(value.getUID()), value);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to insert value with id " + value.getUID(), exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> updateInDataSource(ID id, T value) {
        return insert(value);
    }

    @Override
    public CompletableFuture<Void> upsertInDataSource(T value) {
        return insert(value);
    }

    @Override
    public CompletableFuture<Void> removeFromDataSource(T value) {
        return removeFromId(value.getUID());
    }

    @Override
    public CompletableFuture<Void> removeFromIdInDataSource(ID id) {
        return CompletableFuture.runAsync(() -> {
            final File file = getFileFor(id);

            if (file.exists() && !file.delete()) {
                QuestPlugin.logger().warning("Failed to delete file: " + file.getAbsolutePath());
            }

            fileCache.remove(id);
        });
    }

    @Override
    public void close() {
        super.close();
        fileCache.clear();
    }

    protected CompletableFuture<Void> removeIf(Predicate<T> predicate) {
        return selectAll().thenComposeAsync(list -> CompletableFuture.allOf(
                list.stream().filter(predicate).map(this::remove).toArray(CompletableFuture[]::new))
        );
    }

    private File getFileFor(ID id) {
        return fileCache.computeIfAbsent(id, identification -> new File(folder, identification.toString() + ".json"));
    }

    private T fromFile(File file, Class<T> tClass) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return GSON.fromJson(reader, tClass);
        }
    }

    private void writeToFile(File file, T value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            GSON.toJson(value, writer);
        }
    }
}
