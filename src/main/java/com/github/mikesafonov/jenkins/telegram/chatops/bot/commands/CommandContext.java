package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
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
    private final boolean authorized;
    private final TelegramBotSender sender;

    public CommandContext(Update update, boolean authorized,
                          TelegramBotSender sender, TelegramBotProperties telegramBotProperties) {
        this.update = update;
        this.authorized = authorized;
        this.commandText = postProcessText(telegramBotProperties, update.getMessage().getText());
        this.args = parseArgs();
        this.sender = sender;
    }

    public Long getChatId() {
        return update.getMessage().getChatId();
    }

    private String postProcessText(TelegramBotProperties telegramBotProperties, String text) {
        return text.replace("@" + telegramBotProperties.getName(), "");
    }

    private String[] parseArgs() {
        String[] commandWithArgs = commandText.split(" ");
        if (commandWithArgs.length == 1) {
            return new String[0];
        } else {
            String[] tmp = new String[commandWithArgs.length - 1];
            System.arraycopy(commandWithArgs, 1, tmp, 0, commandWithArgs.length - 1);
            return tmp;
        }
    }
}
