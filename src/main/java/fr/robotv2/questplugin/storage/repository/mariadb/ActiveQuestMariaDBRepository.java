package fr.robotv2.questplugin.storage.repository.mariadb;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ActiveQuestMariaDBRepository extends MariaDBGenericRepository<UUID, ActiveQuestDto> implements ActiveQuestRepository {

    public ActiveQuestMariaDBRepository(MariaDbDatabaseManager manager) {
        super(ActiveQuestDto.class, "active_quests", manager);
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup questGroup) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = ds.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableName + " WHERE JSON_UNQUOTE(JSON_EXTRACT(data, '$.groupId')) = ?")) {
                statement.setString(1, questGroup.getGroupId());
                statement.executeUpdate();
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.WARNING, "An error occurred while removing active quests for group", exception);
            }
        });
    }

    @Override
    public CompletableFuture<Collection<ActiveQuestDto>> fetchByPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = ds.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE JSON_UNQUOTE(JSON_EXTRACT(data, '$.owner')) = ?")) {
                statement.setString(1, uuid.toString());
                final Set<ActiveQuestDto> activeQuests = new HashSet<>();
                final ResultSet set = statement.executeQuery();
                while(set.next()) {
                    final ActiveQuestDto dto = GSON.fromJson(set.getString("data"), ActiveQuestDto.class);
                    activeQuests.add(dto);
                }
                return activeQuests;
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.WARNING, "An error occurred while fetching active quests for player", exception);
            }

            return Collections.emptyList();
        });
    }
}
