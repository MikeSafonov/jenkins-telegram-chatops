package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
public class TelegramBotSenderTest {
    private TelegramBotProperties telegramBotProperties;
    private TelegramBotSender telegramBotSender;

    @BeforeEach
    void setUp(){
        DefaultBotOptions botOptions = new DefaultBotOptions();
        telegramBotProperties = mock(TelegramBotProperties.class);
        telegramBotSender = new TelegramBotSender(botOptions, telegramBotProperties);
    }

    @Test
    void shouldReturnExpectedToken(){
        when(telegramBotProperties.getToken()).thenReturn("token");

        assertEquals("token", telegramBotSender.getBotToken());
    }
}
