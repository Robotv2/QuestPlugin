package fr.robotv2.questplugin.storage.repository;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.GlobalQuestDto;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GlobalQuestRepository extends Repository<UUID, GlobalQuestDto> {

    CompletableFuture<Collection<GlobalQuestDto>> selectAll(QuestGroup group);

    CompletableFuture<Void> remove(QuestGroup group);
}
