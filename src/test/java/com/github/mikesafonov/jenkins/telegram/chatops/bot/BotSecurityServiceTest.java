package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
public class BotSecurityServiceTest {
    private TelegramBotProperties telegramBotProperties;
    private BotSecurityService botSecurityService;

    @BeforeEach
    void setUp() {
        telegramBotProperties = mock(TelegramBotProperties.class);
        botSecurityService = new BotSecurityService(telegramBotProperties);
    }

    @Test
    void shouldReturnFalseBecauseNoMessageAndNoCallback() {
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(null);
        when(update.getCallbackQuery()).thenReturn(null);

        assertFalse(botSecurityService.isAllowed(update));
    }

    @Test
    void shouldReturnFalseBecauseMessageUserNotInList() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(telegramBotProperties.getUsers()).thenReturn(List.of(2L, 3L));
        when(message.getChatId()).thenReturn(1L);
        when(update.getMessage()).thenReturn(message);

        assertFalse(botSecurityService.isAllowed(update));
    }

    @Test
    void shouldReturnFalseBecauseCallbackUserNotInList() {
        Update update = mock(Update.class);
        CallbackQuery query = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(telegramBotProperties.getUsers()).thenReturn(List.of(2L, 3L));
        when(update.getMessage()).thenReturn(null);
        when(query.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1);
        when(update.getCallbackQuery()).thenReturn(query);

        assertFalse(botSecurityService.isAllowed(update));
    }

    @Test
    void shouldReturnTrueBecauseMessageUserInList() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(telegramBotProperties.getUsers()).thenReturn(List.of(1L, 3L));
        when(message.getChatId()).thenReturn(1L);
        when(update.getMessage()).thenReturn(message);

        assertTrue(botSecurityService.isAllowed(update));
    }

    @Test
    void shouldReturnTrueBecauseCallbackUserInList() {
        Update update = mock(Update.class);
        CallbackQuery query = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(telegramBotProperties.getUsers()).thenReturn(List.of(1L, 3L));
        when(update.getMessage()).thenReturn(null);
        when(query.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1);
        when(update.getCallbackQuery()).thenReturn(query);

        assertTrue(botSecurityService.isAllowed(update));
    }
}
