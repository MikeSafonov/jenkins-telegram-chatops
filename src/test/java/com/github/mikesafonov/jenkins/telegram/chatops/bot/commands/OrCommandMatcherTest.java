package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class OrCommandMatcherTest {

    @Test
    void shouldMatchAll() {
        EqualsCommandMatcher equalsCommandMatcher = new EqualsCommandMatcher("/one");
        NoArgsCommandMatcher noArgsCommandMatcher = new NoArgsCommandMatcher();

        OrCommandMatcher andCommandMatcher = new OrCommandMatcher(List.of(equalsCommandMatcher, noArgsCommandMatcher));

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/one");
        CommandContext context = mock(CommandContext.class);
        when(context.getUpdate()).thenReturn(update);
        when(context.getArgs()).thenReturn(null);

        assertTrue(andCommandMatcher.match(context));

    }

    @Test
    void shouldMatchWhenOneNotMatch() {
        EqualsCommandMatcher equalsCommandMatcher = new EqualsCommandMatcher("/one");
        NoArgsCommandMatcher noArgsCommandMatcher = new NoArgsCommandMatcher();

        OrCommandMatcher andCommandMatcher = new OrCommandMatcher(List.of(equalsCommandMatcher, noArgsCommandMatcher));

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/two");
        CommandContext context = mock(CommandContext.class);
        when(context.getUpdate()).thenReturn(update);
        when(context.getArgs()).thenReturn(null);

        assertTrue(andCommandMatcher.match(context));

    }

    @Test
    void shouldNonMatchWhenBothNotMatch() {
        EqualsCommandMatcher equalsCommandMatcher = new EqualsCommandMatcher("/one");
        NoArgsCommandMatcher noArgsCommandMatcher = new NoArgsCommandMatcher();

        OrCommandMatcher andCommandMatcher = new OrCommandMatcher(List.of(equalsCommandMatcher, noArgsCommandMatcher));

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/two");
        CommandContext context = mock(CommandContext.class);
        when(context.getUpdate()).thenReturn(update);
        when(context.getArgs()).thenReturn(new String[]{"one"});

        assertFalse(andCommandMatcher.match(context));

    }

}
