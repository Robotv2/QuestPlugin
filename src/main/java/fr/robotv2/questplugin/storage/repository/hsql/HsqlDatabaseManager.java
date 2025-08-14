package fr.robotv2.questplugin.storage.repository.hsql;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class HsqlDatabaseManager extends AbstractDatabaseManager {

    private final HikariDataSource dataSource;

    private final QuestPlayerHsqlRepository questPlayerRepository;
    private final ActiveQuestHsqlRepository activeQuestRepository;
    private final ActiveTaskHsqlRepository activeTaskRepository;

    public HsqlDatabaseManager(QuestPlugin plugin) {
        super(plugin);
        installHsqlDriver();
        final File file = new File(plugin.getDataFolder(), "database.hsql");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while creating database file", exception);
            }
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:hsqldb:file:" + file.getAbsolutePath());
            config.setDriverClassName("org{}hsqldb{}jdbc{}JDBCDriver".replace("{}", "."));
            config.setUsername("SA");
            config.setPassword("");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTestQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
            dataSource = new HikariDataSource(config);
            QuestPlugin.logger().info("HSQLDB has been initialized successfully.");
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initialize HSQLDB DataSource", exception);
        }

        this.questPlayerRepository = new QuestPlayerHsqlRepository(this);
        this.activeQuestRepository = new ActiveQuestHsqlRepository(this);
        this.activeTaskRepository = new ActiveTaskHsqlRepository(this);

        checkVersion();
    }

    private void checkVersion() {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            final ResultSet set = statement.executeQuery("SELECT DATABASE_VERSION() FROM INFORMATION_SCHEMA.SYSTEM_USERS");
            if (set.next()) {
                String version = set.getString(1);
                QuestPlugin.logger().info("Connected to HSQLDB version " + version);
            }
        } catch (SQLException exception) {
            QuestPlugin.logger().log(Level.SEVERE, "An error occurred while checking database version", exception);
        }
    }

    private void installHsqlDriver() {
        Library lib = Library.builder()
                .groupId("org{}hsqldb")
                .artifactId("hsqldb")
                .version("2.7.2")
                .build();
        QuestPlugin.logger().info("Loading HSQLDB driver");
        QuestPlugin.libraryManager().loadLibrary(lib);
        QuestPlugin.logger().info("HSQLDB driver has been loaded successfully.");
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
