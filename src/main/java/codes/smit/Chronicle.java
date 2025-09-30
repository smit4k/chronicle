package codes.smit;

import codes.smit.listeners.ArchiveListener;
import codes.smit.listeners.CommandListener;

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

        JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Chronicle())
                .addEventListeners(new ArchiveListener())
                .addEventListeners(new CommandListener())
                .build();
    }
}