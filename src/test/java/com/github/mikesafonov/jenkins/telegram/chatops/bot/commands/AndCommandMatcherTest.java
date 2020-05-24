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
class AndCommandMatcherTest {

    @Test
    void shouldMatchAll() {
        EqualsCommandMatcher equalsCommandMatcher = new EqualsCommandMatcher("/one");
        NoArgsCommandMatcher noArgsCommandMatcher = new NoArgsCommandMatcher();

        AndCommandMatcher andCommandMatcher = new AndCommandMatcher(List.of(equalsCommandMatcher, noArgsCommandMatcher));

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
    void shouldNonMatchWhenOneNotMatch() {
        EqualsCommandMatcher equalsCommandMatcher = new EqualsCommandMatcher("/one");
        NoArgsCommandMatcher noArgsCommandMatcher = new NoArgsCommandMatcher();

        AndCommandMatcher andCommandMatcher = new AndCommandMatcher(List.of(equalsCommandMatcher, noArgsCommandMatcher));

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/two");
        CommandContext context = mock(CommandContext.class);
        when(context.getUpdate()).thenReturn(update);
        when(context.getArgs()).thenReturn(null);

        assertFalse(andCommandMatcher.match(context));

    }

}
