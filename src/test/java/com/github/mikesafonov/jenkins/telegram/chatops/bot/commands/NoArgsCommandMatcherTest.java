package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class NoArgsCommandMatcherTest {

    private NoArgsCommandMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new NoArgsCommandMatcher();
    }

    @Test
    void shouldMatchWhenNoArgs() {
        CommandContext context = mock(CommandContext.class);
        when(context.getArgs()).thenReturn(new String[0]);

        assertTrue(matcher.match(context));
    }

    @Test
    void shouldMatchWhenArgsIsNull() {
        CommandContext context = mock(CommandContext.class);
        when(context.getArgs()).thenReturn(null);

        assertTrue(matcher.match(context));
    }

    @Test
    void shouldNonMatchWhenArgsExists() {
        CommandContext context = mock(CommandContext.class);
        when(context.getArgs()).thenReturn(new String[]{"one", "two"});

        assertFalse(matcher.match(context));
    }


}
