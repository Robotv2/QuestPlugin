package fr.robotv2.questplugin.storage.repository.sarah;

import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.logger.JULogger;
import fr.maxlego08.sarah.logger.Logger;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.AbstractDatabaseManager;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

public class SarahDatabaseManager extends AbstractDatabaseManager {

    private final DatabaseConnection connection;

    private final QuestPlayerSarahRepository questPlayerSarahRepository;
    private final ActiveQuestSarahRepository activeQuestSarahRepository;
    private final ActiveTaskSarahRepository activeTaskSarahRepository;

    public SarahDatabaseManager(QuestPlugin plugin, DatabaseConnection connection) {
        super(plugin);
        this.connection = connection;
        final Logger logger = JULogger.from(QuestPlugin.logger());

        MigrationManager.registerMigration(new QuestPlayerSarahRepository.TableMigration());
        MigrationManager.registerMigration(new ActiveQuestSarahRepository.TableMigration());
        MigrationManager.registerMigration(new ActiveTaskSarahRepository.TableMigration());
        MigrationManager.execute(connection, logger);

        final RequestHelper helper = new RequestHelper(connection, logger);
        this.questPlayerSarahRepository = new QuestPlayerSarahRepository(this, helper);
        this.activeQuestSarahRepository = new ActiveQuestSarahRepository(this, helper);
        this.activeTaskSarahRepository = new ActiveTaskSarahRepository(this, helper);
    }

    public DatabaseConfiguration getConfiguration() {
        return getConnection().getDatabaseConfiguration();
    }

    public DatabaseConnection getConnection() {
        return connection;
    }

    @Override
    public QuestPlayerRepository getQuestPlayerRepository() {
        return questPlayerSarahRepository;
    }

    @Override
    public ActiveQuestRepository getActiveQuestRepository() {
        return activeQuestSarahRepository;
    }

    @Override
    public ActiveTaskRepository getActiveTaskRepository() {
        return activeTaskSarahRepository;
    }
}
