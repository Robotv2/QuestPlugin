package fr.robotv2.questplugin.storage.repository;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.model.QuestPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ActiveQuestRepository extends Repository<UUID, ActiveQuestDto> {
    CompletableFuture<Void> remove(QuestGroup group);
}
