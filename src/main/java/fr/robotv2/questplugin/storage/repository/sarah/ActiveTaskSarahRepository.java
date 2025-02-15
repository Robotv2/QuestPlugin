package fr.robotv2.questplugin.storage.repository.sarah;

import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.database.Migration;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ActiveTaskSarahRepository implements ActiveTaskRepository {

    public static final String TABLE = "active_task";

    private final SarahDatabaseManager manager;
    private final RequestHelper helper;

    public ActiveTaskSarahRepository(SarahDatabaseManager manager, RequestHelper helper) {
        this.manager = manager;
        this.helper = helper;
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup group) {
        return CompletableFuture.runAsync(() -> {
            helper.delete(TABLE, (table) -> {
                table.string("parent_quest_group_id", group.getGroupId());
            });
        });
    }

    @Override
    public CompletableFuture<Optional<ActiveTaskDto>> select(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final List<ActiveTaskDto> dtos = helper.select(TABLE, ActiveTaskDto.class, (table) -> {
                table.uuid("id", uuid).primary();
            });
            return dtos.isEmpty() ? Optional.empty() : Optional.of(dtos.get(0));
        });
    }

    @Override
    public CompletableFuture<List<ActiveTaskDto>> selectAll() {
        return CompletableFuture.supplyAsync(() -> helper.selectAll(TABLE, ActiveTaskDto.class));
    }

    @Override
    public CompletableFuture<Void> insert(ActiveTaskDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.insert(TABLE, ActiveTaskDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> update(UUID uuid, ActiveTaskDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.update(TABLE, ActiveTaskDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> upsert(ActiveTaskDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.upsert(TABLE, ActiveTaskDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> remove(ActiveTaskDto value) {
        return removeFromId(value.getId());
    }

    @Override
    public CompletableFuture<Void> removeFromId(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            helper.delete(TABLE, (table) -> {
                table.uuid("id", uuid);
            });
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
                table.uuid("id").primary();
                table.string("parent_quest_id", 16);
                table.string("parent_quest_group_id", 16);
                table.integer("task_id");
                table.decimal("progress");
                table.decimal("required");
                table.bool("done");
            });
        }
    }
}
