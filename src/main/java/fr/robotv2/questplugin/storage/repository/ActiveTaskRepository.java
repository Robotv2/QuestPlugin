package fr.robotv2.questplugin.storage.repository;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ActiveTaskRepository extends Repository<UUID, ActiveTaskDto> {
    CompletableFuture<Void> remove(QuestGroup group);
}
