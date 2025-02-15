package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;

import java.io.File;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ActiveTaskJsonRepository extends GenericJsonRepository<UUID, ActiveTaskDto> implements ActiveTaskRepository {

    public ActiveTaskJsonRepository(File folder) {
        super(folder, ActiveTaskDto.class);
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup group) {
        return removeIf((task) -> Objects.equals(task.getParentQuestGroupId(), group.getGroupId()));
    }
}
