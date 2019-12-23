package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Mike Safonov
 */
@Service
@RequiredArgsConstructor
public class BotSecurityService {
    private final TelegramBotProperties telegramBotProperties;

    public boolean isAllowed(Long chatId) {
        return telegramBotProperties.getUsers().contains(chatId);
    }
}
