package fr.robotv2.questplugin.storage.repository.sarah;

import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.database.Migration;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ActiveQuestSarahRepository implements ActiveQuestRepository {

    public static final String TABLE = "active_quest";

    private final SarahDatabaseManager manager;
    private final RequestHelper helper;

    public ActiveQuestSarahRepository(SarahDatabaseManager manager, RequestHelper helper) {
        this.manager = manager;
        this.helper = helper;
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup group) {
        return CompletableFuture.runAsync(() -> {
            helper.delete(TABLE, (table) -> {
                table.string("group_id", group.getGroupId());
            });
        });
    }

    @Override
    public CompletableFuture<Optional<ActiveQuestDto>> select(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final List<ActiveQuestDto> dtos =  helper.select(TABLE, ActiveQuestDto.class, (table) -> {
                table.uuid("id", uuid).primary();
            });
            return dtos.isEmpty() ? Optional.empty() : Optional.of(dtos.get(0));
        });
    }

    @Override
    public CompletableFuture<List<ActiveQuestDto>> selectAll() {
        return CompletableFuture.supplyAsync(() -> helper.selectAll(TABLE, ActiveQuestDto.class));
    }

    @Override
    public CompletableFuture<Void> insert(ActiveQuestDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.insert(TABLE, ActiveQuestDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> update(UUID uuid, ActiveQuestDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.update(TABLE, ActiveQuestDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> upsert(ActiveQuestDto value) {
        return CompletableFuture.runAsync(() -> {
            helper.upsert(TABLE, ActiveQuestDto.class, value);
        });
    }

    @Override
    public CompletableFuture<Void> remove(ActiveQuestDto value) {
        return removeFromId(value.getId());
    }

    @Override
    public CompletableFuture<Void> removeFromId(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            helper.delete(TABLE, (table) -> table.uuid("id", uuid));
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
                table.uuid("owner");
                table.string("quest_id", 16);
                table.string("group_id", 16);
                table.bigInt("next_reset");
                table.blob("active_tasks");
                table.bool("started");
            });
        }
    }
}
