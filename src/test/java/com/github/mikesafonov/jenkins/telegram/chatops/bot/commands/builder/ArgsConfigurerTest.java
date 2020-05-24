package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.ArgsLengthCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.ArgsMinLengthCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.NoArgsCommandMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Mike Safonov
 */
class ArgsConfigurerTest {

    private ArgsConfigurer argsConfigurer;
    private CommandConfigurer commandConfigurer;

    @BeforeEach
    void setUp() {
        commandConfigurer = mock(CommandConfigurer.class);
        argsConfigurer = new ArgsConfigurer(commandConfigurer);
    }

    @Nested
    class NoArgs {

        @Test
        void shouldAddNoArgsMatcher() {
            argsConfigurer.noArgs();

            ArgumentCaptor<CommandMatcher> captor = ArgumentCaptor.forClass(CommandMatcher.class);
            verify(commandConfigurer).addMatcher(captor.capture());

            CommandMatcher value = captor.getValue();
            assertThat(value).isInstanceOf(NoArgsCommandMatcher.class);
        }

        @Test
        void shouldReturnCommandConfigurer() {
            CommandConfigurer actual = argsConfigurer.noArgs();

            assertThat(actual).isEqualTo(commandConfigurer);
        }
    }

    @Nested
    class Length {

        @Test
        void shouldAddLengthMatcher() {
            argsConfigurer.length(5);

            verify(commandConfigurer).addMatcher(new ArgsLengthCommandMatcher(5));
        }

        @Test
        void shouldReturnCommandConfigurer() {
            CommandConfigurer actual = argsConfigurer.length(5);

            assertThat(actual).isEqualTo(commandConfigurer);
        }

    }

    @Nested
    class MinLength {

        @Test
        void shouldAddMinLengthMatcher() {
            argsConfigurer.minLength(5);

            verify(commandConfigurer).addMatcher(new ArgsMinLengthCommandMatcher(5));
        }

        @Test
        void shouldReturnCommandConfigurer() {
            CommandConfigurer actual = argsConfigurer.minLength(5);

            assertThat(actual).isEqualTo(commandConfigurer);
        }

    }

}
