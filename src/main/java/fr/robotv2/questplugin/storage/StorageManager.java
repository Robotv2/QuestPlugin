package fr.robotv2.questplugin.storage;

import java.util.List;
import java.util.Optional;

public interface StorageManager<ID, T extends Identifiable<ID>> {

    Optional<T> select(ID id);

    List<T> selectAll();

    void insert(T value);

    void update(ID id, T value);

    void upsert(T value);

    void remove(T value);

    void removeFromId(ID id);

    void close();

}
