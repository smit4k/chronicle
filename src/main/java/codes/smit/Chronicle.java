package codes.smit;

import codes.smit.database.DatabaseManager;
import codes.smit.database.MessageRepository;
import codes.smit.listeners.ArchiveListener;
import codes.smit.listeners.CommandListener;
import codes.smit.services.ArchiveService;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import io.github.cdimascio.dotenv.Dotenv;

public class Chronicle extends ListenerAdapter {

    public static void main(String[] args) {
        // Use environment variable for security
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");

        if (token == null) {
            System.err.println("DISCORD_TOKEN not found in .env!");
            return;
        }


        DatabaseManager dbManager = new DatabaseManager();
        MessageRepository messageRepository = new MessageRepository(dbManager);
        ArchiveService archiveService = new ArchiveService(messageRepository);


        JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Chronicle())
                .addEventListeners(new ArchiveListener(archiveService))
                .addEventListeners(new CommandListener())
                .build();
    }
}