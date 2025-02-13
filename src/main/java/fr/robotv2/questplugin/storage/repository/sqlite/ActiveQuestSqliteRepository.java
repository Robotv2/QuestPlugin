package fr.robotv2.questplugin.storage.repository.sqlite;

import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.database.Migration;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.util.Futures;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ActiveQuestSqliteRepository implements ActiveQuestRepository {

    public static final String TABLE = "active_quest";

    private final SQLiteDatabaseManager manager;
    private final RequestHelper helper;

    public ActiveQuestSqliteRepository(SQLiteDatabaseManager manager, RequestHelper helper) {
        this.manager = manager;
        this.helper = helper;
    }

    @Override
    public CompletableFuture<Void> remove(QuestPlayer questPlayer, QuestGroup group) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for(ActiveQuest active : questPlayer.getActiveQuests(group)) {
            futures.add(remove(new ActiveQuestDto(active)));
        }
        return Futures.ofAll(futures);
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
    public CompletableFuture<Void> removeIfEnded(QuestPlayer questPlayer) {
        return CompletableFuture.runAsync(() -> {

        });
    }

    @Override
    public CompletableFuture<Optional<ActiveQuestDto>> select(UUID uuid) {
        return null;
    }

    @Override
    public CompletableFuture<List<ActiveQuestDto>> selectAll() {
        return null;
    }

    @Override
    public CompletableFuture<Void> insert(ActiveQuestDto value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> update(UUID uuid, ActiveQuestDto value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> upsert(ActiveQuestDto value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> remove(ActiveQuestDto value) {
        return null;
    }

    @Override
    public CompletableFuture<Void> removeFromId(UUID uuid) {
        return null;
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
