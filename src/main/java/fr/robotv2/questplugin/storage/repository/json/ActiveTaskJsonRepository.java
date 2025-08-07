package fr.robotv2.questplugin.storage.repository.json;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;

import java.io.File;
import java.util.Collection;
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

    @Override
    public CompletableFuture<Collection<ActiveTaskDto>> fetchByActiveQuest(UUID activeQuestId) {
        return selectAll().thenApply((list) -> {
            return list.stream().filter((task) -> Objects.equals(task.getParentActiveQuestId(), activeQuestId)).toList();
        });
    }

    @Override
    public CompletableFuture<Multimap<UUID, ActiveTaskDto>> bulkFetchByActiveQuest(Collection<UUID> activeQuestIds) {
        return selectAll().thenApply((list) -> {
            final Multimap<UUID, ActiveTaskDto> result = ArrayListMultimap.create();
            for (ActiveTaskDto task : list) {
                if (activeQuestIds.contains(task.getUID())) {
                    result.put(task.getUID(), task);
                }
            }
            return result;
        });
    }
}
