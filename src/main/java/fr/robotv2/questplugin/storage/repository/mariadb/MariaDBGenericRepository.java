package fr.robotv2.questplugin.storage.repository.mariadb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.repository.Repository;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MariaDBGenericRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T> {

    public static final Gson GSON;

    static {
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);
        builder.disableHtmlEscaping();
        builder.serializeNulls();
        GSON = builder.create();
    }

    protected final Class<T> tClass;
    protected final String tableName;
    protected final HikariDataSource ds;

    public MariaDBGenericRepository(Class<T> tClass, String tableName, MariaDbDatabaseManager manager) {
        this.tClass = tClass;
        this.tableName = tableName;
        this.ds = manager.getDataSource();
    }

    @Override
    public CompletableFuture<Optional<T>> select(ID id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT data FROM " + tableName + " WHERE id = ?")) {
                stmt.setObject(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String json = rs.getString("data");
                        return Optional.of(GSON.fromJson(json, tClass));
                    }
                }
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while selecting data from the database.", exception);
            }

            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<List<T>> selectAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<T> results = new ArrayList<>();
            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT data FROM " + tableName);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String json = rs.getString("data");
                    results.add(GSON.fromJson(json, tClass));
                }
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while selecting data from the database.", exception);
            }
            return results;
        });
    }

    @Override
    public CompletableFuture<Void> insert(T t) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + tableName + " (id, data) VALUES (?, ?)")) {
                stmt.setObject(1, t.getUID());
                stmt.setString(2, GSON.toJson(t));
                stmt.executeUpdate();
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while inserting data into the database.", exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> update(ID id, T t) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE " + tableName + " SET data = ? WHERE id = ?")) {
                stmt.setString(1, GSON.toJson(t));
                stmt.setObject(2, id);
                stmt.executeUpdate();
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while updating data in the database.", exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> upsert(T t) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO " + tableName + " (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data)")) {
                stmt.setObject(1, t.getUID());
                stmt.setString(2, GSON.toJson(t));
                stmt.executeUpdate();
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while upserting data into the database.", exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> remove(T t) {
        return removeFromId(t.getUID());
    }

    @Override
    public CompletableFuture<Void> removeFromId(ID id) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            } catch (SQLException exception) {
                QuestPlugin.logger().log(Level.SEVERE, "An error occurred while removing data from the database.", exception);
            }
        });
    }

    @Override
    public void init() {
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS " + tableName + " (id VARCHAR(36) PRIMARY KEY, data TEXT NOT NULL)")) {
            stmt.executeUpdate();
        } catch (SQLException exception) {
            QuestPlugin.logger().log(Level.SEVERE, "An error occurred while initializing the database.", exception);
        }
    }

    @Override
    public void close() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
            QuestPlugin.logger().log(Level.INFO, "MariaDB database connection has been closed.");
        }
    }
}
