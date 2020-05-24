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
class ArgsMinLengthCommandMatcherTest {

    private ArgsMinLengthCommandMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new ArgsMinLengthCommandMatcher(2);
    }

    @Test
    void shouldMatchWhenLengthEquals() {
        CommandContext context = mock(CommandContext.class);
        when(context.getArgs()).thenReturn(new String[]{"one", "two"});

        assertTrue(matcher.match(context));
    }

    @Test
    void shouldMatchWhenLengthGreater() {
        CommandContext context = mock(CommandContext.class);
        when(context.getArgs()).thenReturn(new String[]{"one", "two", "three"});

        assertTrue(matcher.match(context));
    }

    @Test
    void shouldNonMatchWhenLengthLower() {
        CommandContext context = mock(CommandContext.class);
        when(context.getArgs()).thenReturn(new String[]{"one"});

        assertFalse(matcher.match(context));
    }

    @Test
    void shouldNonMatchWhenArgsIsNull() {
        CommandContext context = mock(CommandContext.class);
        when(context.getArgs()).thenReturn(null);

        assertFalse(matcher.match(context));
    }


}
