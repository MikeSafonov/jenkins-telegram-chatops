package com.github.mikesafonov.jenkins.telegram.chatops;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Mike Safonov
 */
@Log4j2
@Service
public class ChatopsTelegramBot extends TelegramLongPollingBot {
    private final TelegramBotProperties telegramBotProperties;

    public ChatopsTelegramBot(DefaultBotOptions botOptions, TelegramBotProperties telegramBotProperties) {
        super(botOptions);
        this.telegramBotProperties = telegramBotProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(update);
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }
}
