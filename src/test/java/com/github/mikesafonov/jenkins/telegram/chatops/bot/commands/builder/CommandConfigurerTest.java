package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.AndCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Mike Safonov
 */
class CommandConfigurerTest {

    private CommandConfigurer commandConfigurer;
    private CommandsBuilder builder;
    private CommandMatcher initMatcher;

    @BeforeEach
    void setUp() {
        builder = mock(CommandsBuilder.class);
        initMatcher = mock(CommandMatcher.class);

        commandConfigurer = new CommandConfigurer(builder, initMatcher);
    }

    @Nested
    class Creation {

        @Test
        void shouldAddInitMatcher() {
            assertThat(commandConfigurer).extracting("matchers").isEqualTo(List.of(initMatcher));
        }
    }

    @Nested
    class Action {

        private Consumer<CommandContext> action = context -> {
        };

        @Test
        void shouldSetUpAction() {
            commandConfigurer.action(action);

            assertThat(commandConfigurer).extracting("commandAction").isEqualTo(action);
        }

        @Test
        void shouldReturnSelf() {
            assertThat(commandConfigurer.action(action)).isEqualTo(commandConfigurer);
        }
    }

    @Nested
    class And {

        private Consumer<CommandContext> action = context -> {
        };

        @Test
        void shouldRegisterCommand() {

            commandConfigurer
                .action(action)
                .and();

            Command command = new Command(new AndCommandMatcher(List.of(initMatcher)), action);

            verify(builder).registerCommand(command);

        }

        @Test
        void shouldReturnBuilder() {
            assertThat(commandConfigurer.and()).isEqualTo(builder);
        }
    }

}
