package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * @author Mike Safonov
 */
@Service
@RequiredArgsConstructor
public class BotSecurityService {
    private final TelegramBotProperties telegramBotProperties;

    public boolean isAllowed(Update update) {
        if (update.getMessage() != null) {
            return isKnownUser(update.getMessage().getChatId());
        }
        if (update.getCallbackQuery() != null) {
            User user = update.getCallbackQuery().getFrom();
            return isKnownUser(Long.valueOf(user.getId()));
        }
        return false;
    }

    private boolean isKnownUser(Long userId) {
        return telegramBotProperties.getUsers().contains(userId);
    }
}
