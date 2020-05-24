package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.EqualsCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.StartsWithCommandMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Mike Safonov
 */
class CommandsBuilderTest {

    private CommandsBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new CommandsBuilder();
    }

    @Nested
    class CommandMethod {

        @Test
        void shouldReturnExpectedCommandConfigurer() {
            CommandConfigurer configurer = builder.command("value");

            assertThat(configurer).extracting("matchers").isEqualTo(List.of(new EqualsCommandMatcher("value")));
            assertThat(configurer).extracting("builder").isEqualTo(builder);
        }
    }

    @Nested
    class CommandStartsWith {

        @Test
        void shouldReturnExpectedCommandConfigurer() {
            CommandConfigurer configurer = builder.commandStartsWith("value");

            assertThat(configurer).extracting("matchers").isEqualTo(List.of(new StartsWithCommandMatcher("value")));
            assertThat(configurer).extracting("builder").isEqualTo(builder);
        }
    }

    @Nested
    class Build {

        @Test
        void shouldReturnExpected() {
            Command command = mock(Command.class);

            builder.registerCommand(command);

            assertThat(builder.build()).containsOnly(command);
        }
    }

}
