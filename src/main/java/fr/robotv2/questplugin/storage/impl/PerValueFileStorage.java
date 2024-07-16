package fr.robotv2.questplugin.storage.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.StorageManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PerValueFileStorage<ID, T extends Identifiable<ID>> implements StorageManager<ID, T> {

    private final Map<ID, T> valueCache = new ConcurrentHashMap<>();
    private final Map<ID, File> fileCache = new ConcurrentHashMap<>();

    private final File folder;
    private final Class<T> tClass;

    private final Gson gson;

    public PerValueFileStorage(File folder, Class<T> tClass) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.folder = folder;
        this.tClass = tClass;

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .create();
    }

    private File getFileFor(ID id) {
        return fileCache.computeIfAbsent(id, identification -> new File(folder, identification.toString() + ".json"));
    }

    @Override
    public Optional<T> select(ID id) {
        return Optional.ofNullable(valueCache.computeIfAbsent(id, k -> {
            try {
                return fromFile(getFileFor(id), tClass);
            } catch (FileNotFoundException fileNotFoundException) {
                return null;
            } catch (IOException exception) {
                throw new RuntimeException("Failed to load data with id " + id, exception);
            }
        }));
    }

    @Override
    public List<T> selectAll() {

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
                    valueCache.put(value.getId(), value);
                    fileCache.put(value.getId(), file);
                }
            } catch (IOException exception) {
                throw new RuntimeException("Failed to load data from file " + file.getAbsolutePath(), exception);
            }
        }

        return allValues;
    }

    @Override
    public void insert(T value) {
        try {
            writeToFile(getFileFor(value.getId()), value);
            valueCache.put(value.getId(), value);
        } catch (IOException exception) {
            throw new RuntimeException("An error occurred while inserting value with id " + value.getId(), exception);
        }
    }

    @Override
    public void update(ID id, T value) {
        insert(value);
    }

    @Override
    public void remove(T value) {
        removeFromId(value.getId());
    }

    @Override
    public void removeFromId(ID id) {
        final File file = getFileFor(id);

        if (file.exists() && !file.delete()) {
            QuestPlugin.logger().warning("Failed to delete file: " + file.getAbsolutePath());
        }

        valueCache.remove(id);
        fileCache.remove(id);
    }

    @Override
    public void close() {
        valueCache.clear();
        fileCache.clear();
    }

    private T fromFile(File file, Class<T> tClass) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return gson.fromJson(reader, tClass);
        }
    }

    private void writeToFile(File file, T value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(value, writer);
        }
    }
}
