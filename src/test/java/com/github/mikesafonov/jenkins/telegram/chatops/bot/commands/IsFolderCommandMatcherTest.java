package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class IsFolderCommandMatcherTest {
    @Test
    void shouldReturnTrueWhenCommandStartsWithEmoji() {
        var matcher = new IsFolderCommandMatcher();
        var context = mock(CommandContext.class);
        when(context.getCommandText()).thenReturn(BotEmoji.FOLDER_UNICODE + " hello");

        assertThat(matcher.match(context)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCommandNotStartWithEmoji() {
        var matcher = new IsFolderCommandMatcher();
        var context = mock(CommandContext.class);
        when(context.getCommandText()).thenReturn(" hello");

        assertThat(matcher.match(context)).isFalse();
    }
}
