package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Mike Safonov
 */
@Getter
@EqualsAndHashCode
@ToString
public class CommandContext {

    private final Update update;
    private final String commandText;
    private final String[] args;
    private final TelegramBotSender sender;
    private final UserState state;
    private final Long chatId;

    public CommandContext(Update update,
                          TelegramBotSender sender, TelegramBotProperties telegramBotProperties,
                          UserState state,
                          Long chatId) {
        this.update = update;
        this.commandText = postProcessText(telegramBotProperties,
                (update.getMessage() == null) ? "":
                update.getMessage().getText());
        this.args = commandText.split(" ");
        this.sender = sender;
        this.state = state;
        this.chatId = chatId;
    }

    private String postProcessText(TelegramBotProperties telegramBotProperties, String text) {
        return text.replace("@" + telegramBotProperties.getName(), "");
    }
}
