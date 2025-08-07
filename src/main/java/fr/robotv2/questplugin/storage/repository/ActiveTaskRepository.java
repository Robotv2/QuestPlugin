package fr.robotv2.questplugin.storage.repository;

import com.google.common.collect.Multimap;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ActiveTaskRepository extends Repository<UUID, ActiveTaskDto> {

    CompletableFuture<Void> remove(QuestGroup group);

    CompletableFuture<Collection<ActiveTaskDto>> fetchByActiveQuest(UUID activeQuestId);

    CompletableFuture<Multimap<UUID, ActiveTaskDto>> bulkFetchByActiveQuest(Collection<UUID> activeQuestIds);
}
