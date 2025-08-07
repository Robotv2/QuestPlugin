package fr.robotv2.questplugin.storage.repository.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.QuestPluginConfiguration;
import fr.robotv2.questplugin.storage.AbstractDatabaseManager;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import net.byteflux.libby.Library;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class MariaDbDatabaseManager extends AbstractDatabaseManager {

    private final HikariDataSource dataSource;

    private final QuestPlayerMariaDBRepository questPlayerRepository;
    private final ActiveQuestMariaDBRepository activeQuestRepository;
    private final ActiveTaskMariaDBRepository activeTaskRepository;

    public MariaDbDatabaseManager(QuestPlugin plugin) {
        super(plugin);
        installMariaDbDriver();

        try {
            final QuestPluginConfiguration.MariaDbCredentials credentials = plugin.getQuestConfiguration().getCredentials();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mariadb://" + credentials.getHost() + ":" + credentials.getPort() + "/" + credentials.getDatabase());
            config.setUsername(credentials.getUsername());
            config.setPassword(credentials.getPassword());
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTestQuery("SELECT 1;");
            dataSource = new HikariDataSource(config);
            QuestPlugin.logger().info("MariaDB has been initialized successfully.");
        } catch (Exception exception) {
            throw new RuntimeException("Failed to initialize MariaDB DataSource", exception);
        }

        this.questPlayerRepository = new QuestPlayerMariaDBRepository(this);
        this.activeQuestRepository = new ActiveQuestMariaDBRepository(this);
        this.activeTaskRepository = new ActiveTaskMariaDBRepository(this);

        checkVersion();
    }

    private void checkVersion() {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            final ResultSet set = statement.executeQuery("SELECT version();");
            if (set.next()) {
                String version = set.getString(1);
                QuestPlugin.logger().info("Connected to MariaDB version " + version);
            }
        } catch (SQLException exception) {
            QuestPlugin.logger().log(Level.SEVERE, "An error occurred while checking database version", exception);
        }
    }

    private void installMariaDbDriver() {
        Library lib = Library.builder()
                .groupId("org{}mariadb{}jdbc")
                .artifactId("mariadb-java-client")
                .version("3.2.0")
                .build();
        QuestPlugin.logger().info("Loading MariaDB driver");
        QuestPlugin.libraryManager().loadLibrary(lib);
        QuestPlugin.logger().info("MariaDB driver has been loaded successfully.");
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
