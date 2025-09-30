package codes.smit.services;

import codes.smit.database.MessageRepository;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;

public class ArchiveService {

    private final MessageRepository messageRepository;

    public ArchiveService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void archiveMessage(Message message) {
        User author = message.getAuthor();
        String content = message.getContentRaw();
        String messageId = message.getId();
        String channelName = message.getChannel().getName();
        String timestamp = message.getTimeCreated()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        System.out.println("=== ARCHIVING MESSAGE ===");
        System.out.println("Message ID: " + messageId);
        System.out.println("Author: " + author.getAsTag() + " (" + author.getId() + ")");
        System.out.println("Channel: " + channelName);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Content: " + content);

        if (!message.getAttachments().isEmpty()) {
            System.out.println("Attachments:");
            message.getAttachments().forEach(attachment -> {
                System.out.println("  - " + attachment.getFileName() + " (" + attachment.getUrl() + ")");
            });
        }

        System.out.println("========================\n");


        // Save to message repository
        messageRepository.saveMessage(message);
    }
}