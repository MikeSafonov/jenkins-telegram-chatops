package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class StateCommandMatcherTest {
    @Test
    void shouldReturnTrueWhenStateMatch() {
        var matcher = new StateCommandMatcher(UserState.WAIT_COMMAND);
        var context = mock(CommandContext.class);
        when(context.getState()).thenReturn(UserState.WAIT_COMMAND);

        assertThat(matcher.match(context)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStateNotMatch() {
        var matcher = new StateCommandMatcher(UserState.WAIT_COMMAND);
        var context = mock(CommandContext.class);
        when(context.getState()).thenReturn(UserState.WAIT_PARAMETERS);

        assertThat(matcher.match(context)).isFalse();
    }
}
