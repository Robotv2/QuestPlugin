package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.database.DatabaseManager;
import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

import java.io.File;
import java.util.UUID;

public class QuestPlayerJsonRepository extends GenericJsonRepository<UUID, QuestPlayerDto> implements QuestPlayerRepository {

    public QuestPlayerJsonRepository(DatabaseManager manager, File folder) {
        super(manager, folder, QuestPlayerDto.class);
    }
}
