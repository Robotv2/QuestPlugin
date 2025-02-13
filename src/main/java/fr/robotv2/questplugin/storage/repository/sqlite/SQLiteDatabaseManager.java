package fr.robotv2.questplugin.storage.repository.sqlite;

import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.SqliteConnection;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.AbstractDatabaseManager;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.GlobalQuestRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

public class SQLiteDatabaseManager extends AbstractDatabaseManager {

    private final DatabaseConfiguration configuration;
    private final DatabaseConnection connection;

    private final QuestPlayerSqliteRepository questPlayerSqliteRepository;

    public SQLiteDatabaseManager(QuestPlugin plugin) {
        super(plugin);

        this.configuration = DatabaseConfiguration.sqlite(true);
        this.connection = new SqliteConnection(configuration, plugin.getQuestDataFolder());

        this.questPlayerSqliteRepository = new QuestPlayerSqliteRepository(this);
    }

    public DatabaseConfiguration getConfiguration() {
        return configuration;
    }

    public DatabaseConnection getConnection() {
        return connection;
    }

    @Override
    public QuestPlayerRepository getQuestPlayerRepository() {
        return questPlayerSqliteRepository;
    }

    @Override
    public ActiveQuestRepository getActiveQuestRepository() {
        return null;
    }

    @Override
    public ActiveTaskRepository getActiveTaskRepository() {
        return null;
    }

    @Override
    public GlobalQuestRepository getGlobalQuestRepository() {
        return null;
    }
}
