package codes.smit.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();

        jda.updateCommands().addCommands(
                Commands.slash("ping", "Check the bot's response time")
        ).queue();

        System.out.println("Chronicle is ready!");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ping")) {
            event.reply("Pong!").queue();
        }
    }
}