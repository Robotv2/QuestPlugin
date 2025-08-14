package fr.robotv2.questplugin.storage.repository.mariadb;

import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

import java.util.UUID;

public class QuestPlayerMariaDBRepository extends MariaDBGenericRepository<UUID, QuestPlayerDto> implements QuestPlayerRepository {
    public QuestPlayerMariaDBRepository(MariaDbDatabaseManager manager) {
        super(QuestPlayerDto.class, "quest_players", manager);
    }
}
