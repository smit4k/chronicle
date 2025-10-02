package codes.smit.listeners;

import codes.smit.services.ArchiveService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ArchiveListener extends ListenerAdapter {

    private final ArchiveService archiveService;

    public ArchiveListener(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();

        if (content.equalsIgnoreCase(".ac")) {
            Message replyMessage = event.getMessage().getReferencedMessage();

            if (replyMessage == null) {
                event.getMessage().reply("❌ You must reply to a message to archive it!").queue();
                return;
            }

            archiveService.archiveMessage(replyMessage);
            replyMessage.addReaction(Emoji.fromUnicode("📜")).queue();
        }
    }
}