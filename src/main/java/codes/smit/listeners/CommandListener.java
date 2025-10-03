package codes.smit.listeners;

import codes.smit.database.DatabaseManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import codes.smit.database.MessageRepository;
import codes.smit.services.ArchiveService;


public class CommandListener extends ListenerAdapter {

    private final ArchiveService archiveService;

    public CommandListener(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }
    

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();

        jda.updateCommands().addCommands(
                Commands.slash("ping", "Check the bot's response time"),
                Commands.slash("archive_count", "Check the total messages archived")
                        .addOption(OptionType.CHANNEL, "channel", "Count total messages from a specific channel", false)
        ).queue();

        System.out.println("Chronicle is ready!");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ping")) {
            event.reply("Pong!").queue();
        }
        if (event.getName().equals("archive_count")) {
            if (event.getOption("channel") != null) {
                String channelId = event.getOption("channel").getAsChannel().getId();
                String channelName = event.getOption("channel").getAsChannel().getName();

                int total = archiveService.getTotalMessagesFromChannel(channelId);
                event.reply("📊 Messages archived from <#" + channelId + ">: **" + total + "**").queue();
            }
            else {
                int total = archiveService.getTotalMessagesArchived();
                event.reply("📊 Total archived messages: **" + total + "**").queue();
            }
        }
    }
}