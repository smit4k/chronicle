package codes.smit.listeners;

import codes.smit.database.DatabaseManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

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
                Commands.slash("archive_all", "Archive all messages")
                        .addOption(OptionType.CHANNEL, "channel", "Archive all messages from a channel", true),
                // Will add archiving by user later
                Commands.slash("archive_count", "Check the total messages archived")
                        .addOption(OptionType.CHANNEL, "channel", "Count total messages from a specific channel", false)
                        .addOption(OptionType.USER, "user", "Count the amount of messages a user has archived.", false))
                .queue();

        System.out.println("Chronicle is ready!");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ping")) {
            event.reply("Pong!")
                    .setEphemeral(true)
                    .queue();
        }
        if (event.getName().equals("archive_all")) {
            var option = event.getOption("channel");
            if (option == null) {
                event.reply("❌ You must specify a channel.").setEphemeral(true).queue();
                return;
            }

            Channel channel = option.getAsChannel();

            if (!(channel instanceof MessageChannel messageChannel)) {
                event.reply("❌ That channel cannot contain messages. Please select a text channel.")
                        .setEphemeral(true).queue();
                return;
            }

            // Defer the reply to acknowledge the interaction immediately
            event.deferReply().setEphemeral(false).queue();

            // Run archiving asynchronously
            new Thread(() -> {
                archiveService.archiveAllMessages(messageChannel);

                // Send confirmation once done
                event.getHook().sendMessage(
                        "📦 Archiving finished for **#" + messageChannel.getName() + "**.").queue();
            }).start();
        }

        if (event.getName().equals("archive_count")) {
            if (event.getOption("channel") != null) {
                String channelId = event.getOption("channel").getAsChannel().getId();
                String channelName = event.getOption("channel").getAsChannel().getName();

                int total = archiveService.getTotalMessagesFromChannel(channelId);
                event.reply("📊 Messages archived from <#" + channelId + ">: **" + total + "**")
                        .setEphemeral(true)
                        .queue();
            } else if (event.getOption("user") != null) {
                String userId = event.getOption("user").getAsUser().getId();
                int total = archiveService.getTotalMessagesFromUser(userId);
                event.reply("📊 Messages archived from <@" + userId + ">: **" + total + "**")
                        .setEphemeral(true)
                        .queue();
            } else {
                int total = archiveService.getTotalMessagesArchived();
                event.reply("📊 Total archived messages: **" + total + "**")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}
