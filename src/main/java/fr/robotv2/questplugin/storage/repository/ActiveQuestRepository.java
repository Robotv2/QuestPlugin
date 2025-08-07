package fr.robotv2.questplugin.storage.repository;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ActiveQuestRepository extends Repository<UUID, ActiveQuestDto> {

    CompletableFuture<Void> remove(QuestGroup group);

    CompletableFuture<Collection<ActiveQuestDto>> fetchByPlayer(UUID playerId);
}
