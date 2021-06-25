package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

/**
 * @author Mike Safonov
 */
@Log4j2
@Service
public class JenkinsChatopsTelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties telegramBotProperties;
    private final BotSecurityService botSecurityService;
    private final TelegramBotSender sender;
    private final List<Command> commands;

    public JenkinsChatopsTelegramBot(DefaultBotOptions options,
                                     TelegramBotProperties telegramBotProperties,
                                     BotSecurityService botSecurityService,
                                     TelegramBotSender sender,
                                     List<Command> commands) {
        super(options);
        this.telegramBotProperties = telegramBotProperties;
        this.botSecurityService = botSecurityService;
        this.sender = sender;
        this.commands = commands;
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        boolean authorized = botSecurityService.isAllowed(update);
        var context = new CommandContext(update, sender, telegramBotProperties);
        if (!authorized) {
            sender.sendUnauthorized(context.getChatId());
        } else {
            findCommand(context)
                    .ifPresentOrElse(command -> command.getAction().accept(context),
                            () -> sender.sendUnknownCommand(context.getChatId(), context.getCommandText()));
        }
    }

    private Optional<Command> findCommand(CommandContext commandContext) {
        return commands.stream()
            .filter(command -> command.isMatch(commandContext))
            .findFirst();
    }
}
