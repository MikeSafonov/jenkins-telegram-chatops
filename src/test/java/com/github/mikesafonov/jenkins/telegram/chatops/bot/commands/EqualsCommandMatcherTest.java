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
class EqualsCommandMatcherTest {
    private EqualsCommandMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new EqualsCommandMatcher("/help");
    }

    @Test
    void shouldSupport() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/help");
        CommandContext context = mock(CommandContext.class);
        when(context.getUpdate()).thenReturn(update);

        assertTrue(matcher.match(context));
    }

    @Test
    void shouldNotSupport() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/run");
        CommandContext context = mock(CommandContext.class);
        when(context.getUpdate()).thenReturn(update);

        assertFalse(matcher.match(context));
    }

    @Test
    void shouldNotSupportWhenMessageIsNull() {
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(null);
        CommandContext context = mock(CommandContext.class);
        when(context.getUpdate()).thenReturn(update);

        assertFalse(matcher.match(context));
    }
}
