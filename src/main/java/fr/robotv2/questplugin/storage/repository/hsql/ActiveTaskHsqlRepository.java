package fr.robotv2.questplugin.storage.repository.hsql;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ActiveTaskHsqlRepository extends HsqlGenericRepository<UUID, ActiveTaskDto> implements ActiveTaskRepository {

    public ActiveTaskHsqlRepository(HsqlDatabaseManager manager) {
        super(ActiveTaskDto.class, "active_tasks", manager);
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup group) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = ds.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableName + " WHERE data LIKE ?")) {
                statement.setString(1, "%\"parentQuestGroupId\":\"" + group.getGroupId() + "\"%");
                statement.executeUpdate();
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.WARNING, "An error occurred while removing active quests for group", exception);
            }
        });
    }

    @Override
    public CompletableFuture<Collection<ActiveTaskDto>> fetchByActiveQuest(UUID activeQuestId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = ds.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE data LIKE ?")) {
                statement.setString(1, "%\"parentActiveQuestId\":\"" + activeQuestId.toString() + "\"%");
                final Set<ActiveTaskDto> activeTasks = new HashSet<>();
                final ResultSet set = statement.executeQuery();
                while (set.next()) {
                    final ActiveTaskDto dto = GSON.fromJson(set.getString("data"), ActiveTaskDto.class);
                    activeTasks.add(dto);
                }
                return activeTasks;
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.WARNING, "An error occurred while fetching active quests for player", exception);
            }

            return Collections.emptyList();
        });
    }

    @Override
    public CompletableFuture<Multimap<UUID, ActiveTaskDto>> bulkFetchByActiveQuest(Collection<UUID> activeQuestIds) {
        return CompletableFuture.supplyAsync(() -> {
            final Multimap<UUID, ActiveTaskDto> result = HashMultimap.create();
            if (activeQuestIds.isEmpty()) {
                return result;
            }

            String query = "SELECT * FROM " + tableName + " WHERE " +
                    String.join(" OR ", Collections.nCopies(activeQuestIds.size(), "data LIKE ?"));

            try (Connection connection = ds.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                int index = 1;
                for (UUID activeQuestId : activeQuestIds) {
                    statement.setString(index++, "%\"parentActiveQuestId\":\"" + activeQuestId.toString() + "\"%");
                }
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    ActiveTaskDto dto = GSON.fromJson(set.getString("data"), ActiveTaskDto.class);
                    result.put(dto.getParentActiveQuestId(), dto);
                }
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.WARNING, "An error occurred while bulk fetching active tasks", exception);
            }

            return result;
        });
    }
}
