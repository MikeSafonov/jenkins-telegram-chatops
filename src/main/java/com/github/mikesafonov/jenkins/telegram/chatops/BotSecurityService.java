package com.github.mikesafonov.jenkins.telegram.chatops;

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
