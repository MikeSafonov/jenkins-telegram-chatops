package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class CommandTest {

    @Test
    void shouldMatch() {
        CommandContext commandContext = mock(CommandContext.class);
        CommandMatcher matcher = mock(CommandMatcher.class);

        when(matcher.match(commandContext)).thenReturn(true);


        Command command = new Command(false, matcher, context -> {
        });

        assertTrue(command.isMatch(commandContext));
    }

    @Test
    void shouldNonMatch() {
        CommandContext commandContext = mock(CommandContext.class);
        CommandMatcher matcher = mock(CommandMatcher.class);

        when(matcher.match(commandContext)).thenReturn(false);


        Command command = new Command(false, matcher, context -> {
        });

        assertFalse(command.isMatch(commandContext));
    }

}
