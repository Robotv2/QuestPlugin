package fr.robotv2.questplugin.storage.repository;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.model.QuestPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ActiveQuestRepository extends Repository<UUID, ActiveQuestDto> {

    CompletableFuture<Void> remove(QuestPlayer questPlayer, QuestGroup group);

    CompletableFuture<Void> remove(QuestGroup group);

    CompletableFuture<Void> removeIfEnded(QuestPlayer questPlayer);

    @Override
    default CompletableFuture<Void> removeFromId(UUID uuid) {
        throw new RuntimeException("Can't remove active quest from id directly.");
    }
}
