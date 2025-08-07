package fr.robotv2.questplugin.storage.repository.hsql;

import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

import java.util.UUID;

public class QuestPlayerHsqlRepository extends HsqlGenericRepository<UUID, QuestPlayerDto> implements QuestPlayerRepository {
    public QuestPlayerHsqlRepository(HsqlDatabaseManager manager) {
        super(QuestPlayerDto.class, "quest_players", manager);
    }
}
