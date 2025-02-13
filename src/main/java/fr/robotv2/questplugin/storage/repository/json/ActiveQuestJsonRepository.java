package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.storage.DatabaseManager;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.util.Futures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ActiveQuestJsonRepository extends GenericJsonRepository<UUID, ActiveQuestDto> implements ActiveQuestRepository {

    private final DatabaseManager databaseManager;

    public ActiveQuestJsonRepository(DatabaseManager manager, File folder) {
        super(folder, ActiveQuestDto.class);
        this.databaseManager = manager;
    }

    @Override
    public CompletableFuture<Void> remove(QuestPlayer questPlayer, QuestGroup group) {

        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
            if(Objects.equals(activeQuest.getGroupId(), group.getGroupId())) {
                futures.add(remove(new ActiveQuestDto(activeQuest)));
            }
        }

        return Futures.ofAll(futures);
    }

    @Override
    public CompletableFuture<Void> remove(QuestGroup group) {
        return removeIf((activeQuest) -> Objects.equals(group.getGroupId(), activeQuest.getGroupId()));
    }

    @Override
    public CompletableFuture<Void> removeIfEnded(QuestPlayer questPlayer) {

        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        questPlayer.getActiveQuests().removeIf((activeQuest) -> {

            if(!activeQuest.hasEnded()) {
                return false;
            }

            futures.add(remove(new ActiveQuestDto(activeQuest)));
            return true;
        });

        return Futures.ofAll(futures);
    }

    @Override
    public CompletableFuture<Void> removeFromDataSource(ActiveQuestDto quest) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        for(UUID task : quest.getActiveTasks()) {
            futures.add(databaseManager.getActiveTaskRepository().removeFromId(task));
        }

        return Futures.ofAll(futures);
    }

    @Override
    public CompletableFuture<Void> removeFromId(UUID uuid) {
        return select(uuid)
                .thenAccept((optional) -> optional.ifPresent(this::removeFromDataSource))
                .thenCompose((unused) -> super.removeFromId(uuid));
    }
}
