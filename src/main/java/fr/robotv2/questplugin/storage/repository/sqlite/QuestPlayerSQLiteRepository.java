package fr.robotv2.questplugin.storage.repository.sqlite;

import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

import java.util.UUID;

public class QuestPlayerSQLiteRepository extends SQLiteGenericRepository<UUID, QuestPlayerDto> implements QuestPlayerRepository {
    public QuestPlayerSQLiteRepository(SqliteDatabaseManager manager) {
        super(QuestPlayerDto.class, "quest_players", manager);
    }
}
