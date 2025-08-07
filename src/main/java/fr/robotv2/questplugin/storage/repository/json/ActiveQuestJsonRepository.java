package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ActiveQuestJsonRepository extends GenericJsonRepository<UUID, ActiveQuestDto> implements ActiveQuestRepository {

    public ActiveQuestJsonRepository(File folder) {
        super(folder, ActiveQuestDto.class);
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup group) {
        return removeIf((quest) -> Objects.equals(quest.getGroupId(), group.getGroupId()));
    }

    @Override
    public CompletableFuture<Collection<ActiveQuestDto>> fetchByPlayer(UUID playerId) {
        return selectAll().thenApply((list) -> {
            return list.stream().filter((quest) -> Objects.equals(quest.getOwner(), playerId)).toList();
        });
    }
}
