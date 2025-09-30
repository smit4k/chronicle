package codes.smit.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:chronicle.db";
    private Connection connection;

    public DatabaseManager() {
        connect();
        createTables();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database: chronicle.db");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
    }

    private void createTables() {
        String createMessagesTable = """
            CREATE TABLE IF NOT EXISTS archived_messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                message_id TEXT UNIQUE NOT NULL,
                author_id TEXT NOT NULL,
                author_name TEXT NOT NULL,
                author_tag TEXT NOT NULL,
                channel_id TEXT NOT NULL,
                channel_name TEXT NOT NULL,
                server_id TEXT NOT NULL,
                server_name TEXT NOT NULL,
                content TEXT,
                timestamp TEXT NOT NULL,
                is_edited INTEGER DEFAULT 0,
                edited_timestamp TEXT,
                reactions TEXT,
                has_attachments INTEGER DEFAULT 0,
                archived_at TEXT NOT NULL
            );
        """;

        String createAttachmentsTable = """
            CREATE TABLE IF NOT EXISTS attachments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                message_id TEXT NOT NULL,
                filename TEXT NOT NULL,
                file_extension TEXT,
                url TEXT NOT NULL,
                size INTEGER,
                FOREIGN KEY (message_id) REFERENCES archived_messages(message_id)
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMessagesTable);
            stmt.execute(createAttachmentsTable);
            System.out.println("Database tables ready");
        } catch (SQLException e) {
            System.err.println("Failed to create tables: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}