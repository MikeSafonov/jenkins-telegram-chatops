package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class StartsWithUpdateCheckTest {
    private StartsWithUpdateCheck check;

    @BeforeEach
    void setUp() {
        check = new StartsWithUpdateCheck("/help");
    }

    @Test
    void shouldSupport() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/help asdasd");

        assertTrue(check.support(update));
    }

    @Test
    void shouldNotSupport() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/run");

        assertFalse(check.support(update));
    }

    @Test
    void shouldNotSupportWhenMessageIsNull() {
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(null);

        assertFalse(check.support(update));
    }
}
