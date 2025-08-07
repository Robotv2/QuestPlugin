package fr.robotv2.questplugin.storage.repository.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.AbstractDatabaseManager;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import net.byteflux.libby.Library;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SqliteDatabaseManager extends AbstractDatabaseManager {

    private final HikariDataSource dataSource;

    private final QuestPlayerSQLiteRepository questPlayerRepository;
    private final ActiveQuestSQLiteRepository activeQuestRepository;
    private final ActiveTaskSQLiteRepository activeTaskRepository;

    public SqliteDatabaseManager(QuestPlugin plugin) {
        super(plugin);
        installSqliteDriver();
        final File file = new File(plugin.getDataFolder(), "database.db");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while creating database file", exception);
            }
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
            config.setDriverClassName("org.sqlite.JDBC");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTestQuery("SELECT 1;");
            dataSource = new HikariDataSource(config);
            QuestPlugin.logger().info("SQLite has been initialized successfully.");
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initialize SQLite DataSource", exception);
        }

        this.questPlayerRepository = new QuestPlayerSQLiteRepository(this);
        this.activeQuestRepository = new ActiveQuestSQLiteRepository(this);
        this.activeTaskRepository = new ActiveTaskSQLiteRepository(this);

        checkVersion();
    }

    private void checkVersion() {
        try {
            final Statement statement = dataSource.getConnection().createStatement();
            final ResultSet set = statement.executeQuery("SELECT sqlite_version();");
            if (set.next()) {
                String sqliteVersion = set.getString(1);
                QuestPlugin.logger().info("Connected to SQLite version " + sqliteVersion);
            }
        } catch (SQLException exception) {
            QuestPlugin.logger().log(Level.SEVERE, "An error occurred while checking database version", exception);
        }
    }

    private void installSqliteDriver() {
        Library lib = Library.builder()
                .groupId("org{}xerial")
                .artifactId("sqlite-jdbc")
                .version("3.49.1.0")
                .build();
        QuestPlugin.logger().info("Loading SQLite driver");
        QuestPlugin.libraryManager().loadLibrary(lib);
        QuestPlugin.logger().info("SQLite driver has been loaded successfully.");
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public @NotNull QuestPlayerRepository getQuestPlayerRepository() {
        return questPlayerRepository;
    }

    @Override
    public @NotNull ActiveQuestRepository getActiveQuestRepository() {
        return activeQuestRepository;
    }

    @Override
    public @NotNull ActiveTaskRepository getActiveTaskRepository() {
        return activeTaskRepository;
    }
}
