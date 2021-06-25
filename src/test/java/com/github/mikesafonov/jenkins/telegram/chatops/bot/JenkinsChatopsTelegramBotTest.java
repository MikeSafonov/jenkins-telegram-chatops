package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class JenkinsChatopsTelegramBotTest {
    private DefaultBotOptions defaultBotOptions;
    private TelegramBotProperties telegramBotProperties;
    private BotSecurityService botSecurityService;
    private TelegramBotSender telegramBotSender;
    private JenkinsChatopsTelegramBot telegramBot;

    @BeforeEach
    void setUp() {
        defaultBotOptions = new DefaultBotOptions();
        telegramBotProperties = mock(TelegramBotProperties.class);
        botSecurityService = mock(BotSecurityService.class);
        telegramBotSender = mock(TelegramBotSender.class);
    }

    @Nested
    class GetBotToken {
        private JenkinsChatopsTelegramBot telegramBot;

        @BeforeEach
        void setUp() {
            telegramBot = new JenkinsChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                    botSecurityService, telegramBotSender, Collections.emptyList());
        }

        @Test
        void shouldReturnExpectedToken() {
            when(telegramBotProperties.getToken()).thenReturn("token");

            assertEquals("token", telegramBot.getBotToken());
        }
    }

    @Nested
    class GetBotUsername {
        private JenkinsChatopsTelegramBot telegramBot;

        @BeforeEach
        void setUp() {
            telegramBot = new JenkinsChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                    botSecurityService, telegramBotSender, Collections.emptyList());
        }

        @Test
        void shouldReturnExpectedToken() {
            when(telegramBotProperties.getName()).thenReturn("token");

            assertEquals("token", telegramBot.getBotUsername());
        }
    }

    @Nested
    class OnUpdateReceived {
        private final Long chatId = 1L;

        @BeforeEach
        void setUp() {
            telegramBot = new JenkinsChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                    botSecurityService, telegramBotSender, Collections.emptyList());
        }

        @Nested
        class WhenUnauthorized {
            private Command command;
            private JenkinsChatopsTelegramBot bot;

            @BeforeEach
            void setUp() {
                command = mock(Command.class);
                when(command.isMatch(any(CommandContext.class))).thenReturn(true);

                bot = new JenkinsChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                        botSecurityService, telegramBotSender, List.of(command));

                when(botSecurityService.isAllowed(any(Update.class))).thenReturn(false);
            }

            @Test
            void shouldSendUnauthorized() {
                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                bot.onUpdateReceived(update);

                verify(telegramBotSender).sendUnauthorized(chatId);
            }
        }

        @Nested
        class WhenAuthorized {
            private Command command;
            private JenkinsChatopsTelegramBot bot;

            @BeforeEach
            void setUp() {
                command = mock(Command.class);
                when(command.isMatch(any(CommandContext.class))).thenReturn(true);

                bot = new JenkinsChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                        botSecurityService, telegramBotSender, List.of(command));

                when(botSecurityService.isAllowed(any(Update.class))).thenReturn(true);
            }

            @Test
            void shouldCallAction() {
                Consumer<CommandContext> action = mock(Consumer.class);
                when(command.getAction()).thenReturn(action);

                Update update = mock(Update.class);
                Message message = mock(Message.class);
                when(update.getMessage()).thenReturn(message);
                when(message.getChatId()).thenReturn(chatId);
                when(message.getText()).thenReturn("command");

                CommandContext commandContext = new CommandContext(update, telegramBotSender, telegramBotProperties);

                bot.onUpdateReceived(update);

                verify(action).accept(commandContext);
            }

            @Nested
            class WhenNoOneMatch {
                @Test
                void shouldSendUnknownCommand() {
                    Command one = mock(Command.class);
                    Command two = mock(Command.class);
                    List<Command> commands = List.of(one, two);

                    when(one.isMatch(any(CommandContext.class))).thenReturn(false);
                    when(two.isMatch(any(CommandContext.class))).thenReturn(false);

                    var bot = new JenkinsChatopsTelegramBot(defaultBotOptions, telegramBotProperties,
                            botSecurityService, telegramBotSender, commands);
                    Update update = mock(Update.class);
                    Message message = mock(Message.class);
                    when(update.getMessage()).thenReturn(message);
                    when(message.getChatId()).thenReturn(chatId);
                    when(message.getText()).thenReturn("command");

                    bot.onUpdateReceived(update);

                    verify(telegramBotSender).sendUnknownCommand(chatId, "command");
                }
            }
        }
    }

}
