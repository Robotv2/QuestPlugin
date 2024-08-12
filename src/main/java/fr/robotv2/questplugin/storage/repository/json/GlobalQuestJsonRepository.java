package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.GlobalQuestDto;
import fr.robotv2.questplugin.storage.repository.GlobalQuestRepository;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GlobalQuestJsonRepository extends GenericJsonRepository<UUID, GlobalQuestDto> implements GlobalQuestRepository {

    public GlobalQuestJsonRepository(File folder) {
        super(folder, GlobalQuestDto.class);
    }

    @Override
    public CompletableFuture<Collection<GlobalQuestDto>> selectAll(QuestGroup group) {
        return selectAll().thenApply((list) -> list.stream().filter((global) -> global.getGroupId().equals(group.getGroupId())).collect(Collectors.toSet()));
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup group) {
        return removeIf((globalQuest) -> Objects.equals(group.getGroupId(), globalQuest.getGroupId()));
    }
}
