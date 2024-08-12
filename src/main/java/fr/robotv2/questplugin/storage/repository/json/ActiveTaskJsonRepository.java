package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;

import java.io.File;
import java.util.UUID;

public class ActiveTaskJsonRepository extends GenericJsonRepository<UUID, ActiveTaskDto> implements ActiveTaskRepository {
    public ActiveTaskJsonRepository(File folder) {
        super(folder, ActiveTaskDto.class);
    }
}
