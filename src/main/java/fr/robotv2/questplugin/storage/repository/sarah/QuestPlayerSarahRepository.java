package fr.robotv2.questplugin.storage.repository.sarah;

import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.database.Migration;
import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class QuestPlayerSarahRepository implements QuestPlayerRepository {

    public static final String TABLE = "quest_player";

    private final SarahDatabaseManager manager;
    private final RequestHelper helper;

    public QuestPlayerSarahRepository(SarahDatabaseManager manager, RequestHelper helper) {
        this.manager = manager;
        this.helper = helper;
    }

    @Override
    public CompletableFuture<Optional<QuestPlayerDto>> select(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final List<QuestPlayerDto> dtos =  helper.select(TABLE, QuestPlayerDto.class, (table) -> {
                table.uuid("uuid", uuid).primary();
            });
            return dtos.isEmpty() ? Optional.empty() : Optional.of(dtos.get(0));
        });
    }

    @Override
    public CompletableFuture<List<QuestPlayerDto>> selectAll() {
        return CompletableFuture.supplyAsync(() -> helper.selectAll(TABLE, QuestPlayerDto.class));
    }

    @Override
    public CompletableFuture<Void> insert(QuestPlayerDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.insert(TABLE, QuestPlayerDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> update(UUID uuid, QuestPlayerDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.update(TABLE, QuestPlayerDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> upsert(QuestPlayerDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.upsert(TABLE, QuestPlayerDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> remove(QuestPlayerDto value) {
        return CompletableFuture.runAsync(() -> {
           helper.delete(TABLE, (table) -> {
                table.uuid("uuid", value.getId());
           });
        });
    }

    @Override
    public CompletableFuture<Void> removeFromId(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            helper.delete(TABLE, (table) -> table.uuid("uuid", uuid));
        });
    }

    @Override
    public void init() {
    }

    @Override
    public void close() {

    }

    public static class TableMigration extends Migration {
        @Override
        public void up() {
            create(TABLE, (table) -> {
                table.uuid("uuid").primary();
                table.string("name", 16);
                table.blob("active_quests");
                table.blob("quest_done");
            });
        }
    }
}
