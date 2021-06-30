package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class IsRunnableCommandMatcherTest {
    @Test
    void shouldReturnTrueWhenCommandStartsWithEmoji() {
        var matcher = new IsRunnableCommandMatcher();
        var context = mock(CommandContext.class);
        when(context.getCommandText()).thenReturn(BotEmoji.RUNNABLE_UNICODE + " hello");

        assertThat(matcher.match(context)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCommandNotStartWithEmoji() {
        var matcher = new IsRunnableCommandMatcher();
        var context = mock(CommandContext.class);
        when(context.getCommandText()).thenReturn(" hello");

        assertThat(matcher.match(context)).isFalse();
    }
}
