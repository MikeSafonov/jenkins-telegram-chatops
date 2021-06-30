package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class CallbackStartsWithCommandMatcherTest {
    private CallbackStartsWithCommandMatcher matcher = new CallbackStartsWithCommandMatcher("hello");
    private CommandContext context;

    @Nested
    class WhenNoCallbackData {

        @BeforeEach
        void setUp() {
            context = mock(CommandContext.class);
            var update = mock(Update.class);
            when(context.getUpdate()).thenReturn(update);
            when(update.getCallbackQuery()).thenReturn(null);
        }

        @Test
        void shouldReturnFalse() {

            assertThat(matcher.match(context)).isFalse();
        }
    }

    @Nested
    class WhenCallbackDataExist {
        private CallbackQuery callbackQuery;

        @BeforeEach
        void setUp() {
            context = mock(CommandContext.class);
            var update = mock(Update.class);
            callbackQuery = mock(CallbackQuery.class);
            when(context.getUpdate()).thenReturn(update);
            when(update.getCallbackQuery()).thenReturn(callbackQuery);
        }

        @Test
        void shouldReturnFalseWhenDataIsNull() {
            when(callbackQuery.getData()).thenReturn(null);

            assertThat(matcher.match(context)).isFalse();
        }

        @Test
        void shouldReturnFalseWhenDataNotStartsWith() {
            when(callbackQuery.getData()).thenReturn("world");

            assertThat(matcher.match(context)).isFalse();
        }

        @Test
        void shouldReturnTrueWhenDataStartsWith() {
            when(callbackQuery.getData()).thenReturn("hello world");

            assertThat(matcher.match(context)).isTrue();
        }
    }
}
