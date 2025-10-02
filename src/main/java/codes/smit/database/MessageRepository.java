package codes.smit.database;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MessageRepository {

    private final DatabaseManager dbManager;

    public MessageRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void saveMessage(Message message) {
        String sql = """
            INSERT INTO archived_messages (
                message_id, author_id, author_name, author_tag,
                channel_id, channel_name, server_id, server_name,
                content, timestamp, is_edited, edited_timestamp,
                reactions, has_attachments, archived_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            User author = message.getAuthor();

            pstmt.setString(1, message.getId());
            pstmt.setString(2, author.getId());
            pstmt.setString(3, author.getName());
            pstmt.setString(4, author.getAsTag());
            pstmt.setString(5, message.getChannel().getId());
            pstmt.setString(6, message.getChannel().getName());
            pstmt.setString(7, message.getGuild().getId());
            pstmt.setString(8, message.getGuild().getName());
            pstmt.setString(9, message.getContentRaw());
            pstmt.setString(10, message.getTimeCreated()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setInt(11, message.isEdited() ? 1 : 0);
            pstmt.setString(12, message.isEdited() ?
                    message.getTimeEdited().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
            pstmt.setString(13, formatReactions(message.getReactions()));
            pstmt.setInt(14, message.getAttachments().isEmpty() ? 0 : 1);
            pstmt.setString(15, LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            pstmt.executeUpdate();

            // Save attachments if any
            if (!message.getAttachments().isEmpty()) {
                saveAttachments(message);
            }

            System.out.println("✓ Message saved to database: " + message.getId());

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("⚠ Message already archived: " + message.getId());
            } else {
                System.err.println("Failed to save message: " + e.getMessage());
            }
        }
    }

    private void saveAttachments(Message message) {
        String sql = """
            INSERT INTO attachments (message_id, filename, file_extension, url, size)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            for (Message.Attachment attachment : message.getAttachments()) {
                pstmt.setString(1, message.getId());
                pstmt.setString(2, attachment.getFileName());
                pstmt.setString(3, attachment.getFileExtension());
                pstmt.setString(4, attachment.getUrl());
                pstmt.setInt(5, attachment.getSize());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Failed to save attachments: " + e.getMessage());
        }
    }

    private String formatReactions(List<MessageReaction> reactions) {
        if (reactions.isEmpty()) return null;

        return reactions.stream()
                .map(r -> r.getEmoji().getAsReactionCode() + ":" + r.getCount())
                .collect(Collectors.joining(","));
    }

    public int getTotalMessagesArchived() {
        String sql = "SELECT COUNT(*) FROM archived_messages";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get total messages: " + e.getMessage());
        }
        return 0;
    }
}