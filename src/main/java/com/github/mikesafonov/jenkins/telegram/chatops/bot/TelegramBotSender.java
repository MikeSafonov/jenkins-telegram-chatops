package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

/**
 * Class for exposing API for sending messages from telegram bot
 *
 * @author Mike Safonov
 */
@Log4j2
@Service
public class TelegramBotSender extends DefaultAbsSender {
    private final TelegramBotProperties telegramBotProperties;

    protected TelegramBotSender(DefaultBotOptions options, TelegramBotProperties telegramBotProperties) {
        super(options);
        this.telegramBotProperties = telegramBotProperties;
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }

    public void sendUnauthorized(Long chatId) {
        sendTextMessage(chatId, "Unauthorized request");
    }

    public void sendTextMessage(Long chatId, String text) {
        try {
            sendApiMethod(new SendMessage(chatId, text));
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendMarkdownTextMessage(Long chatId, String text) {
        try {
            sendApiMethod(new SendMessage(chatId, text).enableMarkdown(true));
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> T sendMethod(Method method) throws TelegramApiException {
        return sendApiMethod(method);
    }
}
